package com.searesoft.lib;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;
import static com.searesoft.lib.ByRef.IntegerRef;
import static com.searesoft.lib.ByRef.StringRef;

/**
 * XML parser converted from my original Delphi code
 * I couldn't get any standard libraries to work reliably, so I decided to use my own code.
 * I wasted 2 days trying before I just converted this and had it up and running within an hour.
 * This Java version only contains the code to read XML, there's no write methods.
 */
public class XMLParser {
    /**
     * Class that handles XML values
     */
    public class XMLValue {
        private int hash, textHash;
        private String name, text;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            hash = elfHash(name);
        }

        public String text() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
            textHash = elfHash(text);
        }

        XMLValue(String name, String text) {
            setName(name);
            setText(text);
        }

        int hash() {
            return hash;
        }

        int textHash() {
            return textHash;
        }
    }

    /**
     * Class that handles XML tags
     */
    public class XMLTag {
        private enum XMLMarker {OpenTag, CloseTagShort, CloseTag, CloseTagLong, EOF, Identifier, Unknown}

        private String text;
        private XMLTag parent;
        private String name;
        private int textHash, hash;

        private final ArrayList<XMLValue> values = new ArrayList<>();
        private final ArrayList<XMLTag> children = new ArrayList<>();

        public ArrayList<XMLTag> children() {
            return children;
        }

        public ArrayList<XMLValue> values() {
            return values;
        }

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            hash = elfHash(name);
        }

        public String text() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
            textHash = elfHash(text);
        }

        int hash() {
            return hash;
        }

        int textHash() {
            return textHash;
        }

        public XMLValue valueFromName(String name) {
            int hash = elfHash(name);
            for (XMLValue value : values) {
                if (value.hash == hash && value.name.equals(name)) {
                    return value;
                }
            }
            return null;
        }

        public XMLTag nextSiblingFromText(String text) {
            if (parent == null) {
                return null;
            }
            int index = parent.children.indexOf(this);
            int hash = elfHash(text);
            for (int i = index + 1; i < parent.children.size(); i++) {
                if (parent.children.get(i).textHash == hash && parent.children.get(i).text.equals(text)) {
                    return parent.children.get(i);
                }
            }
            return null;
        }

        public XMLTag nextSiblingFromName(String name) {
            if (parent == null) {
                return null;
            }
            int index = parent.children.indexOf(this);
            int hash = elfHash(name);
            for (int i = index + 1; i < parent.children.size(); i++) {
                if (parent.children.get(i).hash == hash && parent.children.get(i).name.equals(name)) {
                    return parent.children.get(i);
                }
            }
            return null;
        }

        public XMLTag childFromName(String name) {
            int hash = elfHash(name);
            for (XMLTag child : children) {
                if (child.hash == hash && child.name.equals(name)) {
                    return child;
                }
            }
            return null;
        }

        public XMLTag childFromText(String text) {
            int hash = elfHash(text);
            for (XMLTag child : children) {
                if (child.textHash == hash && child.text.equals(text)) {
                    return child;
                }
            }
            return null;
        }

        public XMLTag childFromChildText(String name, String text) {
            int textHash = elfHash(text);
            int hash = elfHash(name);
            XMLTag child;
            for (int i = 0; i < children.size(); i++) {
                child = children.get(i);
                for (int j = 0; j < child.children.size(); j++) {
                    if (child.children.get(j).hash == hash && child.children.get(j).name.equals(name) &&
                            child.children.get(j).textHash == textHash && child.children.get(j).text.equals(text)) {
                        return child;
                    }
                }
            }
            return null;
        }

        public boolean tagNameExists(String name) {
            if (name == null || this.name == null) {
                return false;
            }
            if (this.name.equalsIgnoreCase(name)) {
                return true;
            }
            if (parent == null) {
                return false;
            }
            return parent.tagNameExists(name);
        }

        private char charAt(String str, int index) {
            if (index > str.length() - 1 || index < 0) {
                return 0;
            }
            return str.charAt(index);
        }

        private char skipJunk(StringRef data, IntegerRef index, boolean rememberBreaks) {
            while (index.val < data.val.length()) {
                char c = data.val.charAt(index.val);
                if (c > 32) {
                    return c;
                } else if ("\t\r\n ".indexOf(c) > -1) {
                    text += " ";
                }
                index.val++;
            }
            return 0;
        }

        private char skipJunk(StringRef data, IntegerRef index) {
            return skipJunk(data, index, false);
        }

        String decodeInvalidChars(String str) {
            String res = str.replaceAll("\r\n\r\n\r\n", "\r\n\r\n");
            res = res.replaceAll("&amp;", "&");
            res = res.replaceAll("&lt;", "<");
            res = res.replaceAll("&gt;", ">");
            res = res.replaceAll("&apos;", " ");
            res = res.replaceAll("&quot;", "\"");
            return res.trim();
        }

        private boolean parseValue(StringRef data, IntegerRef index) {
            String name = parseString(data, index, true, false);
            if (name.isEmpty()) {
                return false;
            }
            if (skipJunk(data, index) != '=') {
                return false;
            }
            index.val++;
            String value = decodeInvalidChars(parseString(data, index, false, false));
            values.add(new XMLValue(name, value));
            return true;
        }

        private boolean parseName(StringRef data, IntegerRef index) {
            name = parseString(data, index, false, false);
            setName(name.replaceAll("/", ""));
            return !name.isEmpty();
        }

        private String parseString(StringRef data, IntegerRef index, boolean breakOnEquals, boolean rememberBreaks) {
            String extSym = breakOnEquals ? "=<>" : "<>";
            char nxt = skipJunk(data, index, rememberBreaks);
            if (nxt == 0) {
                return "";
            }
            if (nxt == '"') {
                return parseQuotedString(data, index);
            }
            nxt = charAt(data.val, index.val);
            while ((nxt <= 32 || extSym.indexOf(nxt) > -1) && (nxt != 0)) {
                index.val++;
                nxt = charAt(data.val, index.val);
                if (nxt == '<' && charAt(data.val, index.val + 1) == '<') {
                    index.val++;
                    nxt = charAt(data.val, index.val);
                }
            }
            int p = index.val;
            while (nxt > 32 && extSym.indexOf(nxt) < 0) {
                index.val++;
                nxt = charAt(data.val, index.val);
                if (nxt == '<' && charAt(data.val, index.val + 1) == '<') {
                    index.val += 2;
                    nxt = charAt(data.val, index.val);
                }
            }
            return data.val.substring(p, index.val);
        }

        private String parseString(StringRef data, IntegerRef index, boolean breakOnEquals) {
            return parseString(data, index, breakOnEquals, false);
        }

        private String parseString(StringRef data, IntegerRef index) {
            return parseString(data, index, false, false);
        }

        private String parseQuotedString(StringRef data, IntegerRef index, char quoteChar) {
            if (charAt(data.val, index.val) != quoteChar) {
                return "";
            }
            int p = index.val + 1;
            boolean skipNext = true;
            char nxt;
            while (true) {
                if (!skipNext) {
                    if (charAt(data.val, index.val) == '\\' && charAt(data.val, index.val + 1) != quoteChar) {
                        skipNext = true;
                    } else {
                        nxt = charAt(data.val, index.val);
                        if (nxt == quoteChar || nxt == 0) {
                            String res = data.val.substring(p, index.val);
                            index.val++;
                            return res;
                        }
                    }
                } else {
                    skipNext = false;
                }
                index.val++;
            }
        }

        private String parseQuotedString(StringRef data, IntegerRef index) {
            return parseQuotedString(data, index, '"');
        }

        private XMLMarker nextTag(StringRef data, IntegerRef index, IntegerRef next, boolean rememberBreaks) {
            char c = skipJunk(data, index);
            IntegerRef p = new IntegerRef(index.val);
            next.val = p.val;
            if (c == 0) {
                return XMLMarker.EOF;
            }
            if (c == '<') {
                if (charAt(data.val, p.val + 1) == '/') {
                    p.val += 2;
                    String str = parseString(data, index, false, false);
                    if ((str.equalsIgnoreCase(name) && skipJunk(data, index) == '>') || !tagNameExists(str)) {
                        next.val = p.val + 1;
                    }
                    return XMLMarker.CloseTagLong;
                }
            }

            //keep this code until I test the simplified version above
//            if (str != null && (str.toLowerCase().equals(name.toLowerCase()) && skipJunk(data, index) == '>')) {
//                next.val = p.val + 1;
//                return XMLMarker.CloseTagLong;
//            } else {
//                if (tagNameExists(str)) {
//                    return XMLMarker.CloseTagLong;
//                }
//                next.val = p.val + 1;
//                return XMLMarker.CloseTagLong;
//            }

            if (c == '<' && charAt(data.val, p.val + 1) == '!' && charAt(data.val, p.val + 2) == '-' &&
                    charAt(data.val, p.val + 3) == '-') {
                next.val = p.val + 4;
                while (p.val < data.val.length() - 1) {
                    if (charAt(data.val, p.val) == '-' && charAt(data.val, p.val + 1) == '-' && charAt(data.val, p.val + 2) == '>') {
                        index.val = p.val + 3;
                        break;
                    }
                    p.val++;
                }
            }

            if (c == '<' && charAt(data.val, p.val + 1) != '<') {
                next.val = p.val + 1;
                return XMLMarker.OpenTag;
            }

            if (c == '>') {
                next.val = p.val + 1;
                if (charAt(data.val, p.val - 1) == '/') {
                    return XMLMarker.CloseTagShort;
                } else {
                    return XMLMarker.CloseTag;
                }
            }

            if (c == '/' && charAt(data.val, p.val + 1) == '>') {
                next.val = p.val + 2;
                return XMLMarker.CloseTagShort;
            }
            next.val = p.val;
            parseString(data, next, false, false);
            return XMLMarker.Identifier;
        }

        private XMLMarker nextTag(StringRef data, IntegerRef index, IntegerRef next) {
            return nextTag(data, index, next, false);
        }

        private boolean parseXML(StringRef data, IntegerRef index) {
            boolean isLong = false;
            boolean inTag = false;
            IntegerRef next = new IntegerRef(0);
            XMLMarker marker;

            clear();
            if (nextTag(data, index, next) != XMLMarker.OpenTag) {
                return false;
            }
            index.val = next.val;
            if (nextTag(data, index, next) != XMLMarker.Identifier) {
                return false;
            }
            boolean res = parseName(data, index);
            if (!res) {
                return false;
            }

            inTag = true;
            res = false;
            while (true) {
                marker = nextTag(data, index, next, !inTag && isLong && !children.isEmpty());
                if (marker == XMLMarker.EOF) {
                    return false;
                } else if (marker == XMLMarker.CloseTagLong) {
                    if (isLong) index.val = next.val;
                    setText(decodeInvalidChars(text));
                    return true;
                } else if (marker == XMLMarker.CloseTagShort) {
                    res = !isLong && inTag;
                    if (res) index.val = next.val;
                    setText(decodeInvalidChars(text));
                    return res;
                } else if (marker == XMLMarker.OpenTag) {
                    res = isLong ? addChild().parseXML(data, index) : false;
                    if (!res) return false;
                } else if (marker == XMLMarker.CloseTag) {
                    isLong = true;
                    index.val = next.val;
                    setText(decodeInvalidChars(text));
                    inTag = false;
                } else if (marker == XMLMarker.Identifier) {
                    if (inTag) {
                        parseValue(data, index);
                    } else {
                        text += parseString(data, index, false, true);
                        setText(decodeInvalidChars(text));
                    }
                } else if (marker == XMLMarker.Unknown) {
                    return true;
                }
            }
        }

        XMLTag(XMLTag parent) {
            this.parent = parent;
        }

        public XMLTag addChild() {
            XMLTag res = new XMLTag(this);
            children.add(res);
            return res;
        }

        public void clear() {
            children.clear();
            values.clear();
            text = "";
            textHash = 0;
        }
    }

    private final ArrayList<XMLValue> header = new ArrayList<>();
    private final XMLTag root = new XMLTag(null);

    public XMLParser() {
        clear();
    }

    public XMLTag root() {
        return root;
    }

    public ArrayList<XMLValue> header() {
        return header;
    }

    public void clear() {
        root.clear();
        header.clear();
        headerValueFromName("encoding").text = "UTF-8";
    }

    public XMLValue headerValueFromName(String name) {
        int hash = elfHash(name);
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).hash == hash && header.get(i).name.equals(name)) {
                return header.get(i);
            }
        }
        XMLValue res = new XMLValue(name, "");
        header.add(res);
        return res;
    }

    public boolean parse(String data) {
        StringRef dataRef = new StringRef(data);
        IntegerRef p = new IntegerRef(0);
        root.skipJunk(dataRef, p);
        int p2 = -1;
        clear();
        IntegerRef p1 = new IntegerRef(dataRef.val.indexOf("<?"));
        if (p1.val >= p.val) {
            p2 = dataRef.val.indexOf("?>");
            if (p2 < p.val) {
                return false;
            }
            String str = dataRef.val.substring(p1.val + 2, p1.val + 6).toUpperCase();
            if (str.indexOf("XML ") != 0) {
                return false;
            }
            str = "<xml " + dataRef.val.substring(p1.val + 6, p2) + "/>";
            XMLTag tag = new XMLTag(null);
            tag.parseXML(new StringRef(str), p);
            for (int i = 0; i < tag.values.size(); i++) {
                header.add(tag.values.get(i));
                if (tag.values.get(i).name.equals("encoding")) {

//            if (tag.values.get(i).value.equals("UTF-8")) {
//                aData:=UTF8Decode(dta);
//            }
                }
                p.val = data.indexOf("?>") + 2;
            }
            do {
                XMLTag.XMLMarker marker = root.nextTag(dataRef, p, p1);
                if (marker == XMLTag.XMLMarker.OpenTag) {
                    if (!root.addChild().parseXML(dataRef, p)) {
                        return false;
                    }
                } else if (marker == XMLTag.XMLMarker.Identifier) {
                    if (!root.text.isEmpty()) {
                        root.text += " " + root.text + " " + root.parseString(dataRef, p, false, true);
                    }
                } else {
                    root.parseString(dataRef, p, false);
                }
            } while (root.skipJunk(dataRef, p) != 0);
        }
        return true;
    }
}

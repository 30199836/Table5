package com.searesoft.lib;

/**
 * Helper class to allow variable (ByRef) parameters
 */
public class ByRef {

    public static class BooleanRef {
        public boolean val;

        public BooleanRef(boolean val) {
            this.val = val;
        }
    }
    public static class IntegerRef {
        public int val;

        public IntegerRef(int val) {
            this.val = val;
        }
    }

    public static class StringRef {
        public String val;

        public StringRef(String val) {
            this.val = val;
        }
    }
}

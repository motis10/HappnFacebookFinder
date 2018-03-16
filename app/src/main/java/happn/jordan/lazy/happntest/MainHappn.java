package happn.jordan.lazy.happntest;

class MainHappn {
    private final static String id = "FsYV2QfK18a5XLLxkeDI3cZTu0FouAl6";

    static String getDecryptedId() {
        String str = null;
        try {
            str = new BlowfishDecrypter().decrypt(id);
        } catch (Exception ignored) {
        }

        return str;
    }
}

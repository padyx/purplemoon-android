package ch.defiant.purplesky.beans;

public class NullPictureFolder extends PictureFolder {

    private static final long serialVersionUID = 7742540653065381734L;

    public NullPictureFolder() {
        this("Invalid folder");
    }

    public NullPictureFolder(String s) {
        super("", "", s);
    }
}

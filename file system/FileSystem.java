public class FileSystem {
    private Node root;

    public FileSystem() {
        root = new Node("root", true);
    }

    public Node getRoot() {
        return root;
    }

    public Node createFile(String name) {
        return new Node(name, false);
    }

    public Node createDirectory(String name) {
        return new Node(name, true);
    }

    public void addNode(Node parent, Node child) {
        if (parent.isDirectory()) {
            parent.addChild(child);
        }
    }
}

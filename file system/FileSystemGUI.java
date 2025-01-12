import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSystemGUI {
    private JFrame frame;
    private JTree tree;
    private FileSystem fileSystem;

    public FileSystemGUI() {
        fileSystem = new FileSystem();
        initGUI();
    }

    private void initGUI() {
        frame = new JFrame("File System Simulation");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultMutableTreeNode rootNode = createTreeNodes(fileSystem.getRoot());
        tree = new JTree(rootNode);
        frame.add(new JScrollPane(tree), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton addDirButton = new JButton("Add Directory");
        JButton addFileButton = new JButton("Add File");
        JButton renameButton = new JButton("Rename");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    String dirName = JOptionPane.showInputDialog(frame, "Enter directory name:");
                    if (dirName != null && !dirName.trim().isEmpty()) {
                        Node parentNode = (Node) selectedNode.getUserObject();
                        Node newDir = fileSystem.createDirectory(dirName);
                        fileSystem.addNode(parentNode, newDir);
                        DefaultMutableTreeNode newDirNode = new DefaultMutableTreeNode(newDir);
                        selectedNode.add(newDirNode);
                        ((DefaultTreeModel) tree.getModel()).reload(selectedNode);
                    }
                }
            }
        });

        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    String fileName = JOptionPane.showInputDialog(frame, "Enter file name:");
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        Node parentNode = (Node) selectedNode.getUserObject();
                        Node newFile = fileSystem.createFile(fileName);
                        fileSystem.addNode(parentNode, newFile);
                        DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode(newFile);
                        selectedNode.add(newFileNode);
                        ((DefaultTreeModel) tree.getModel()).reload(selectedNode);
                    }
                }
            }
        });

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Node selectedItem = (Node) selectedNode.getUserObject();
                    String newName = JOptionPane.showInputDialog(frame, "Enter new name for " + selectedItem.getName() + ":");
                    if (newName != null && !newName.trim().isEmpty()) {
                        selectedItem = new Node(newName, selectedItem.isDirectory());
                        ((DefaultMutableTreeNode) selectedNode).setUserObject(selectedItem);
                        ((DefaultTreeModel) tree.getModel()).reload(selectedNode.getParent());
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                    if (parentNode != null) {
                        Node parentNodeObj = (Node) parentNode.getUserObject();
                        Node selectedNodeObj = (Node) selectedNode.getUserObject();
                        parentNodeObj.getChildren().remove(selectedNodeObj);
                        ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode rootNode = createTreeNodes(fileSystem.getRoot());
                tree.setModel(new DefaultTreeModel(rootNode));
            }
        });

        panel.add(addDirButton);
        panel.add(addFileButton);
        panel.add(renameButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private DefaultMutableTreeNode createTreeNodes(Node root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
        for (Node child : root.getChildren()) {
            rootNode.add(createTreeNodes(child));
        }
        return rootNode;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileSystemGUI::new);
    }
}
package org.jrd.frontend.MainFrame;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.jrd.backend.core.OutputController;
import org.jrd.backend.decompiling.DecompilerWrapperInformation;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates GUI for attached VM.
 */
public class BytecodeDecompilerView {

    private JPanel BytecodeDecompilerPanel;

    private JSplitPane splitPane;
    private JPanel leftMainPanel;
    private JTextField classesSortField;
    private JTextField searchCodeField;
    private JComboBox topComboBox;
    private JPanel classesPanel;
    private JPanel rightMainPanel;
    private JScrollPane leftScrollPanel;
    private JList<String> filteredClassesJlist;
    private RTextScrollPane bytecodeScrollPane;
    private RSyntaxTextArea bytecodeSyntaxTextArea;
    private ActionListener bytesActionListener;
    private ActionListener classesActionListener;
    private ActionListener rewriteActionListener;
    private String[] classes;

    private boolean splitPaneFirstResize = true;

    /**
     * Constructor creates the graphics and adds the action listeners.
     */

    public JPanel getBytecodeDecompilerPanel(){
        return BytecodeDecompilerPanel;
    }


    public BytecodeDecompilerView(){

        BytecodeDecompilerPanel = new JPanel(new BorderLayout());

        classesPanel = new JPanel(new BorderLayout());

        classesSortField = new JTextField();
        classesSortField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                updateClassList();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                updateClassList();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                updateClassList();
            }
        });

        searchCodeField = new JTextField();
        searchCodeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                searchCode();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                searchCode();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                searchCode();
            }
        });

        classesPanel.add(classesSortField, BorderLayout.NORTH);

        filteredClassesJlist = new JList<>();
        filteredClassesJlist.setFixedCellHeight(20);
        filteredClassesJlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final String name = filteredClassesJlist.getSelectedValue();
                if(name != null || filteredClassesJlist.getSelectedIndex() != -1) {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            try {
                                ActionEvent event = new ActionEvent(this, 1, name);
                                bytesActionListener.actionPerformed(event);
                            } catch (Throwable t) {
                                OutputController.getLogger().log(OutputController.Level.MESSAGE_ALL, t);
                            }
                            return null;
                        }
                    }.execute();
                }
        }});

        JButton overwriteButton = new JButton("Overwrite class");
        overwriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String name = filteredClassesJlist.getSelectedValue();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            ActionEvent event = new ActionEvent(this, 3, name);
                            rewriteActionListener.actionPerformed(event);
                        } catch (Throwable t) {
                            OutputController.getLogger().log(OutputController.Level.MESSAGE_ALL, t);
                        }
                        return null;
                    }
                }.execute();
            }
        });

        JButton topButton = new JButton("Refresh loaded classes list");
        topButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            ActionEvent event = new ActionEvent(this, 2, null);


                            classesActionListener.actionPerformed(event);
                        } catch (Throwable t) {
                            OutputController.getLogger().log(OutputController.Level.MESSAGE_ALL, t);
                        }
                        return null;
                    }
                }.execute();
            }
        });

        topComboBox = new JComboBox<DecompilerWrapperInformation>();
        topComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(filteredClassesJlist.getSelectedIndex() != -1){
                    ActionEvent event = new ActionEvent(this, 1, filteredClassesJlist.getSelectedValue());
                    bytesActionListener.actionPerformed(event);
                }
            }
        });

        bytecodeSyntaxTextArea = new RSyntaxTextArea();
        bytecodeSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        bytecodeSyntaxTextArea.setCodeFoldingEnabled(true);
        bytecodeScrollPane = new RTextScrollPane(bytecodeSyntaxTextArea);

        leftMainPanel = new JPanel();
        leftMainPanel.setLayout(new BorderLayout());
        leftMainPanel.setBorder(new EtchedBorder());

        rightMainPanel = new JPanel();
        rightMainPanel.setLayout(new BorderLayout());
        rightMainPanel.setBorder(new EtchedBorder());

        JPanel topButtonPanel = new JPanel();

        topButtonPanel.setLayout(new BorderLayout());
        topButtonPanel.add(topButton, BorderLayout.WEST);
        topButtonPanel.add(overwriteButton);
        topButtonPanel.add(topComboBox, BorderLayout.EAST);

        leftScrollPanel = new JScrollPane(filteredClassesJlist);
        leftScrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        classesPanel.add(leftScrollPanel);
        leftMainPanel.add(classesPanel);
        rightMainPanel.add(bytecodeScrollPane);
        rightMainPanel.add(searchCodeField, BorderLayout.NORTH);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftMainPanel, rightMainPanel);

        splitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(splitPaneFirstResize){
                    splitPane.setDividerLocation(0.5);
                    splitPaneFirstResize = false;
                }
            }
        });

        BytecodeDecompilerPanel.add(topButtonPanel, BorderLayout.NORTH);
        BytecodeDecompilerPanel.add(splitPane, BorderLayout.CENTER);

        BytecodeDecompilerPanel.setVisible(true);

    }

    private void updateClassList(){
        ArrayList<String> filtered = new ArrayList<>();
        String filter = classesSortField.getText();
        for (String classe: classes){
            if (classe.contains(filter)){
                filtered.add(classe);
            }
        }
        filteredClassesJlist.setListData(filtered.toArray(new String[filtered.size()]));
    }

    /**
     * Sets the unfiltered class list array and invokes an update.
     *
     * @param classesToReload
     */
    public void reloadClassList(String[] classesToReload) {
        classes = classesToReload;
        SwingUtilities.invokeLater(() -> updateClassList());
    }

    /**
     * Sets the decompiled code into JTextArea
     *
     * @param decompiledClass String of source code of decompiler class
     */
    public void reloadTextField(String decompiledClass) {
        final String data = decompiledClass;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                bytecodeSyntaxTextArea.setText(data);
            }
        });
    }

    public void setClassesActionListener(ActionListener listener) {
        classesActionListener = listener;
    }


    public void setBytesActionListener(ActionListener listener) {
        bytesActionListener = listener;
    }
    
    public void setRewriteActionListener(ActionListener rewriteActionListener) {
        this.rewriteActionListener = rewriteActionListener;
    }

    /**
     * Creates a warning table in case of error.
     * @param msg message
     */
    public void handleError(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //JOptionPane.showMessageDialog(getUiComponent().getParent(), msg, " ", JOptionPane.WARNING_MESSAGE);
            }

        });
    }

    public void refreshComboBox(List<DecompilerWrapperInformation> wrappers){
        topComboBox.removeAllItems();
        wrappers.forEach(decompilerWrapperInformation -> {
            if (!decompilerWrapperInformation.isInvalidWrapper()){
                topComboBox.addItem(decompilerWrapperInformation);
            }
        });
    }

    public DecompilerWrapperInformation getSelecteddecompilerWrapperInformation(){
        return (DecompilerWrapperInformation) topComboBox.getSelectedItem();
    }

    /**
     Search string in decompiled code
     */
    private void searchCode() {
        SearchContext context = new SearchContext();
        String match = searchCodeField.getText();
        context.setSearchFor(match);
        context.setWholeWord(false);
        SearchEngine.markAll(bytecodeSyntaxTextArea, context);
        int line = SearchEngine.getNextMatchPos(match, bytecodeSyntaxTextArea.getText(), true, true, false);
        if(line >= 0) {
            bytecodeSyntaxTextArea.setCaretPosition(line);
        }
    }
}

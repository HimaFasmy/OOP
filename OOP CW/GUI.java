import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet.ColorAttribute;
import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.JFormattedTextField;

public class GUI {

    public static void display() {

        JFrame frame = new JFrame();
        frame.setTitle("Westminster skin consultation");
        ImageIcon image = new ImageIcon("img/logo.png");
        frame.setIconImage(image.getImage());

        JPanel nav_bar = new JPanel(new BorderLayout());
        JPanel container = new JPanel();

        nav_bar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nav_bar.setBackground(Color.white);
        nav_bar.setPreferredSize(new Dimension(0, 100));
        nav_bar.setBackground(Color.decode("#FFFFEF"));

        JLabel nav_label = (new JLabel("Welcome to Westminster Consultation", SwingConstants.CENTER));
        nav_label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

        nav_bar.add(nav_label, BorderLayout.CENTER);

        frame.add(nav_bar, BorderLayout.NORTH);
        frame.add(container, BorderLayout.CENTER);

        JTable table = container(container);
        FieldAccessor monitor = new FieldAccessor(table);
        table.addMouseListener(monitor);
        frame.setVisible(true);
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static JTable container(JPanel frame) {

        Font poppin = Font.getFont(Font.MONOSPACED);
        Font sansbald = Font.getFont(Font.MONOSPACED);
        try {
            poppin = Font.createFont(Font.TRUETYPE_FONT, new File("font/poppins.otf"));
            sansbald = Font.createFont(Font.TRUETYPE_FONT, new File("font/SansBland.ttf"));
        } catch (FontFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(poppin);

        ge.registerFont(sansbald);

        JPanel label = new JPanel();
        JPanel jtable = new JPanel();

        BoxLayout layout = new BoxLayout(frame, BoxLayout.Y_AXIS);
        JLabel Label = (new JLabel("To place an appointment, select the following fields: "));
        Label.setFont(sansbald.deriveFont(13f));
        label.setBorder(BorderFactory.createEmptyBorder(25, -400, 25, 0));

        label.add(Label);
        frame.setLayout(layout);
        frame.add(label);

        String[] col_identifier = { "Medical License Number", "Doctor Full name", "Specialization",
                "Contact Number" };

        JTable table = new JTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (String column : col_identifier) {
            model.addColumn(column);
        }
        model.addRow(col_identifier);

        String[][] sorter = new String[SkinConsultationManager.doc.size()][4];
        for (int i = 0; i < sorter.length; i++) {
            sorter[i][0] = String.format("%04d", SkinConsultationManager.doc.get(i).doctor_id());
            sorter[i][1] = SkinConsultationManager.doc.get(i).doctor_fname() + " "
                    + SkinConsultationManager.doc.get(i).doctor_surname();
            sorter[i][2] = SkinConsultationManager.doc.get(i).doctor_specialisation();
            sorter[i][3] = SkinConsultationManager.doc.get(i).doctor_mob();
        }

        jtable.setLayout(new GridBagLayout());
        for (int i = 0; i < SkinConsultationManager.doc.size(); i++) {
            model.addRow(new Object[] {
                    sorter[i][0], sorter[i][1],
                    sorter[i][2], sorter[i][3]
            });
        }

        table.setPreferredScrollableViewportSize(new Dimension(0, 10));
        table.setRowHeight(20);
        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        table.setRowHeight(0, 30);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                return this;
            }
        });

        table.setVisible(true);
        table.setShowGrid(true);
        table.setRowSelectionAllowed(true);

        jtable.add(table);

        frame.add(jtable);

        JButton sort = new JButton("Sort Table");
        sort.setFont(poppin.deriveFont(14f));

        sort.setForeground(Color.white);
        sort.setBackground(Color.black);
        sort.addActionListener(e -> sortRow(model, sorter));
        sort.setPreferredSize(new Dimension(200, 25));
        JPanel sortPanel = new JPanel();
        sortPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 100, 0));
        sortPanel.add(sort);
        frame.add(sortPanel);

        return table;
    }

    public static void sortRow(DefaultTableModel table, String[][] sorter) {
        while (true) {
            if (table.getRowCount() > 1)
                table.removeRow(1);
            else
                break;
        }
        Arrays.sort(sorter, new Comparator<String[]>() {

            @Override
            public int compare(String[] o1, String[] o2) {
                return o1[1].compareTo(o2[1]);
            }
        });

        for (int i = 0; i < SkinConsultationManager.doc.size(); i++) {
            table.addRow(new Object[] {
                    sorter[i][0], sorter[i][1],
                    sorter[i][2], sorter[i][3]
            });
        }

    };

}

class FieldAccessor implements MouseListener {

    private JTable table;

    public FieldAccessor(JTable table) {
        this.table = table;
        System.out.println("Validating");
    }

    public void mouseClicked(MouseEvent e) {

        int row = table.rowAtPoint(e.getPoint());
        if (row > 0) {
            Availability availability = new Availability(this.table.getValueAt(row,
                    1).toString());
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

}

class Availability {

    private JFrame frame;

    public Availability(String name) {
        this.frame = new JFrame();
        frame.setTitle("Creating an Appointment with " + name);
        frame.setLayout(new BorderLayout());

        display(name);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(600, 300));
    }

    public static boolean doctorSearch(String name, LocalDate selectDate, JComboBox<String> comboBox) {
        File file = new File("Consultation.txt");
        try {
            Scanner sc = new Scanner(file);
            int count = 1;
            while (sc.hasNextLine()) {
                if (count % 3 != 0) {
                    String consultant = sc.nextLine();
                    String[] date_time = sc.nextLine().split(" ");

                    if (consultant.equals(name)) {
                        if (date_time[0].equals(selectDate.toString())
                                && date_time[1].equals(comboBox.getSelectedItem().toString()))
                            return false;
                        break;
                    }
                    count += 2;
                } else {
                    count = 1;
                    sc.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

    public void display(String name) {

        Font poppin = Font.getFont(Font.MONOSPACED);
        Font sansbald = Font.getFont(Font.MONOSPACED);
        try {
            poppin = Font.createFont(Font.TRUETYPE_FONT, new File("font/poppins.otf"));
            sansbald = Font.createFont(Font.TRUETYPE_FONT, new File("font/SansBland.ttf"));
        } catch (FontFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel date = new JLabel("Date: ", SwingConstants.CENTER);
        date.setFont(poppin.deriveFont(12f));
        panel.setBorder(BorderFactory.createEmptyBorder(75, 0, 0, 0));
        date.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        dateField.setPreferredSize(new Dimension(100, 20));

        JPanel time = new JPanel();
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(poppin.deriveFont(12f));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        String[] timing = new String[8];
        Date t = new Date();
        for (int i = 0; i < 8; i++) {
            try {
                t = new SimpleDateFormat("HH:mm").parse(String.format("%02d:00", i +
                        8));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            timing[i] = new SimpleDateFormat("HH:mm").format(t);
        }
        JComboBox<String> comboBox = new JComboBox<>(timing);
        comboBox.setPreferredSize(new Dimension(100, 20));
        comboBox.setMaximumRowCount(4);
        comboBox.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comboBox.getSelectedItem().toString();
            }
        });

        JPanel submit_btn = new JPanel();
        JButton submit = new JButton("Submit");
        submit.setFont(poppin.deriveFont(14f));
        submit.setPreferredSize(new Dimension(100, 30));
        submit.setBackground(Color.black);
        submit.setForeground(Color.decode("#ffffff"));
        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (dateField.getText().length() > 0) {
                    try {
                        Date date = dateFormat.parse(dateField.getText());
                        LocalDate current_date = LocalDate.now();
                        LocalDate selectDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        if (selectDate.isBefore(current_date)) {
                            JOptionPane.showMessageDialog(null,
                                    "Date is before the current date, Please enter a valid date");
                        } else {

                            String[] options = { "<- Return to edit", "Proceed ->" };
                            String status_code = "Available";

                            File FileObj = new File("Consultation.txt");
                            if (FileObj.createNewFile()) {

                            } else {
                                try {
                                    Scanner sc = new Scanner(FileObj);
                                    int count = 1;
                                    while (sc.hasNextLine()) {
                                        if (count % 3 != 0) {
                                            String consultant = sc.nextLine();
                                            String[] date_time = sc.nextLine().split(" ");

                                            if (consultant.equals(name)) {
                                                if (date_time[0].equals(selectDate.toString())
                                                        && date_time[1].equals(comboBox.getSelectedItem().toString()))

                                                    status_code = "Unavailable";
                                                break;
                                            }
                                            count += 2;
                                        } else {
                                            count = 1;
                                            sc.nextLine();
                                        }
                                    }
                                } catch (FileNotFoundException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }

                            int status = JOptionPane.showOptionDialog(null,
                                    String.format(
                                            "<html>Appointment with Dr. %s <br> Schedule for %s at %s<br> Status: %s</html>",
                                            name,
                                            selectDate,
                                            comboBox.getSelectedItem().toString(),
                                            status_code),

                                    "Confirmation",
                                    0,

                                    JOptionPane.INFORMATION_MESSAGE,
                                    null,
                                    options, null);

                            if (status > 0) {
                                boolean valid = false;
                                if (status_code.equals("Available")) {
                                    Patient_registration appointment = new Patient_registration(name, selectDate,
                                            comboBox.getSelectedItem().toString());
                                    valid = true;
                                    frame.dispose();
                                } else {
                                    String specialization = "";
                                    for (Doctor doctor : SkinConsultationManager.doc) {
                                        if ((doctor.doctor_fname() + " " + doctor.doctor_surname()).equals(name)) {
                                            specialization = doctor.doctor_specialisation();
                                            break;
                                        }
                                    }

                                    for (Doctor doctor : SkinConsultationManager.doc) {
                                        String random_doc = doctor.doctor_fname() + " " + doctor.doctor_surname();
                                        if (specialization.equals(doctor.doctor_specialisation())) {
                                            if (doctorSearch(random_doc, selectDate, comboBox)) {
                                                Patient_registration appointment = new Patient_registration(random_doc,
                                                        selectDate, comboBox.getSelectedItem().toString());
                                                JOptionPane.showMessageDialog(null,
                                                        String.format("Re-allocating appointment to Dr. %s",
                                                                random_doc));
                                                frame.dispose();
                                                valid = true;
                                            }

                                        }

                                    }
                                    if (!valid) {
                                        JOptionPane.showMessageDialog(null,
                                                "Unable to reserve any Doctors at the time period",
                                                "Error", JOptionPane.ERROR_MESSAGE, null);
                                    }

                                }

                            }

                        }
                    } catch (ParseException e3) {
                        JOptionPane.showMessageDialog(null, "Not a valid date");
                    } catch (IOException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter a date in appropriate format");
                }
            }

        });
        submit_btn.add(submit);

        time.add(timeLabel);
        time.add(comboBox);
        time.add(date);
        time.add(dateField);
        panel.add(time);
        panel.add(submit_btn);
        frame.add(panel);
        frame.setVisible(true);
    }

}

class Patient_registration {

    private String doctor_name;
    private String appointment_date;
    private String appointment_time;

    public Patient_registration(String name, LocalDate selectDate, String time) {
        this.doctor_name = name;
        this.appointment_date = selectDate.toString();
        this.appointment_time = time;

        JFrame frame = new JFrame();
        frame.setTitle("Patient registrations");
        patient_form(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void patient_form(JFrame frame) {

        Font poppin = Font.getFont(Font.MONOSPACED);
        Font sansbald = Font.getFont(Font.MONOSPACED);
        try {
            poppin = Font.createFont(Font.TRUETYPE_FONT, new File("font/poppins.otf"));
            sansbald = Font.createFont(Font.TRUETYPE_FONT, new File("font/SansBland.ttf"));
        } catch (FontFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JPanel main_panel = new JPanel(new BorderLayout());
        JPanel sub_panel1 = new JPanel();
        JPanel sub_panel2 = new JPanel();

        sub_panel1.setBackground(Color.decode("#a12803"));
        sub_panel1.setPreferredSize(new Dimension(100, 0));

        sub_panel2.setBackground(Color.decode("#252526"));
        sub_panel2.setLayout(new GridLayout(9, 1));
        sub_panel2.setBorder(BorderFactory.createEmptyBorder(80, 0, -150, 100));

        JPanel name = new JPanel();
        JPanel errorName = new JPanel();
        name.setBackground(Color.decode("#252526"));
        JLabel nameLabel = new JLabel("Ful Name:");
        nameLabel.setFont(poppin.deriveFont(12f));
        JLabel nameErrorLabel = new JLabel();
        nameErrorLabel.setFont(sansbald.deriveFont(10f));
        nameErrorLabel.setForeground(Color.decode("#DD0000"));
        errorName.setBackground(Color.decode("#252526"));
        errorName.add(nameErrorLabel);
        errorName.setForeground(Color.white);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 10));
        nameLabel.setForeground(Color.white);
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(150, 20));
        name.add(nameLabel);
        name.add(nameField);
        name.add(errorName);
        sub_panel2.add(name);

        JPanel dateOfBirth = new JPanel();
        JPanel dobError = new JPanel();
        dateOfBirth.setBackground(Color.decode("#252526"));
        JLabel dateOfBirthLabel = new JLabel("Date of Birth:");
        dateOfBirth.setFont(poppin.deriveFont(12f));
        JLabel dobErrorLabel = new JLabel();
        dobErrorLabel.setFont(sansbald.deriveFont(10f));
        dobErrorLabel.setForeground(Color.decode("#DD0000"));
        dobError.setBackground(Color.decode("#252526"));
        dobError.add(dobErrorLabel);
        dateOfBirthLabel.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 10));
        dateOfBirthLabel.setForeground(Color.white);
        JFormattedTextField dateField = new JFormattedTextField();
        dateField.setPreferredSize(new Dimension(150, 20));
        dateOfBirth.add(dateOfBirthLabel);
        dateOfBirth.add(dateField);
        dateOfBirth.add(dobError);
        sub_panel2.add(dateOfBirth);

        JPanel mobile_number = new JPanel();
        JPanel errorMobile = new JPanel();
        mobile_number.setBackground(Color.decode("#252526"));
        JLabel mobileNumberLabel = new JLabel("Mobile Number:");
        mobileNumberLabel.setFont(poppin.deriveFont(12f));
        JLabel mobileErrorLabel = new JLabel();
        mobileErrorLabel.setFont(sansbald.deriveFont(10f));
        mobileErrorLabel.setForeground(Color.decode("#DD0000"));
        mobileNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        errorMobile.setBackground(Color.decode("#252526"));
        errorMobile.add(mobileErrorLabel);

        mobileNumberLabel.setForeground(Color.white);
        JTextField mobileNumberField = new JTextField();
        mobileNumberField.setPreferredSize(new Dimension(150, 20));
        mobile_number.add(mobileNumberLabel);
        mobile_number.add(mobileNumberField);
        mobile_number.add(errorMobile);
        sub_panel2.add(mobile_number);

        JPanel ID = new JPanel();
        JPanel errorID = new JPanel();
        ID.setBackground(Color.decode("#252526"));
        JLabel IDLabel = new JLabel("ID number:");
        IDLabel.setFont(poppin.deriveFont(12f));
        JLabel IDErrorLabel = new JLabel();
        IDErrorLabel.setFont(sansbald.deriveFont(10f));
        IDErrorLabel.setForeground(Color.decode("#DD0000"));
        errorID.setBackground(Color.decode("#252526"));
        errorID.add(IDErrorLabel);
        IDLabel.setBorder(BorderFactory.createEmptyBorder(0, 45, 0, 10));
        IDLabel.setForeground(Color.white);
        JTextField IDField = new JTextField();
        IDField.setPreferredSize(new Dimension(150, 20));
        ID.add(IDLabel);
        ID.add(IDField);
        ID.add(errorID);
        sub_panel2.add(ID);

        JPanel notes = new JPanel();
        notes.setBackground(Color.decode("#252526"));
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(poppin.deriveFont(12f));
        notesLabel.setBorder(BorderFactory.createEmptyBorder(0, 55, 0, 10));
        notesLabel.setForeground(Color.white);
        JTextArea notesField = new JTextArea();
        notesField.setPreferredSize(new Dimension(150, 80));
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        notes.add(notesLabel);
        notes.add(notesField);
        sub_panel2.add(notes);

        JPanel uploadPanel = new JPanel();
        JButton uploadButton = new JButton("Upload Image");
        uploadButton.setFont(poppin.deriveFont(12f));
        JFileChooser fileChooser = new JFileChooser();
        ArrayList<File> files = new ArrayList<File>(10);
        uploadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.showOpenDialog(frame);

                final File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File file : selectedFiles) {
                    files.add(file);
                }
                if (selectedFiles.length > 0) {
                    if (FileNavigation(selectedFiles, frame))
                        JOptionPane.showMessageDialog(frame, "Image successfully attached", "Attachment",
                                JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });

        uploadButton.setBackground(Color.decode("#00DD00"));
        uploadButton.setForeground(Color.white);
        uploadPanel.add(uploadButton);

        sub_panel2.add(uploadPanel);
        uploadPanel.setBorder(BorderFactory.createEmptyBorder(-2, 135, 0, 0));
        uploadPanel.setBackground(Color.decode("#252526"));

        JPanel btn = new JPanel();
        btn.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));

        btn.setBackground(Color.decode("#252526"));
        JButton button = new JButton("Submit");
        button.setFont(poppin.deriveFont(12f));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean[] status = { nameValidate(nameLabel, nameField, nameErrorLabel),
                        dobValidate(dateOfBirthLabel, dateField, dobErrorLabel),
                        mobileNumberValidate(mobileNumberLabel, mobileNumberField, mobileErrorLabel),
                        idValidate(IDLabel, IDField, IDErrorLabel) };
                if (status[0] && status[1] && status[2] && status[3]) {

                    String[] name = nameField.getText().split(" ");
                    Patient patient = new Patient(name[0], name[1], dateField.getText(),
                            mobileNumberField.getText(), IDField.getText());
                    File file = new File("Consultation.txt");
                    if (notesField.getText().isEmpty()) {
                        notesField.setText("--None--");
                    }
                    try {
                        Consultation consultation;
                        if (file.createNewFile()) {
                            consultation = new Consultation(appointment_date, appointment_time, 15.00,
                                    notesField.getText());
                        } else {
                            consultation = new Consultation(appointment_date, appointment_time, 25.00,
                                    notesField.getText());
                        }
                        summary(consultation, doctor_name, patient, file, files, frame);
                        frame.setVisible(false);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }

            }
        });

        button.setBackground(Color.decode("#CC0000"));
        button.setForeground(Color.decode("#FFFFFF"));
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.decode("#333333"), 1),
                new EmptyBorder(10, 10, 10, 10)));

        button.setFont(new Font("Sans Serif", Font.BOLD, 15));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(100, 30));
        btn.add(button);
        sub_panel2.add(btn);
        main_panel.add(sub_panel1, BorderLayout.WEST);
        main_panel.add(sub_panel2, BorderLayout.CENTER);
        frame.add(main_panel);

    }

    public static boolean nameValidate(JLabel nameLabel, JTextField nameField, JLabel errorLabel) {
        String[] name = nameField.getText().split(" ");
        if (nameField.getText().length() > 0) {
            for (char chr : nameField.getText().toCharArray()) {
                if (!Character.isAlphabetic(chr) && !Character.isSpaceChar(chr)) {
                    errorLabel.setText("<html>*Should contain only<br>alphabetical characters</html>");
                    nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 175, 0, 0));
                    return false;
                } else if (name.length != 2) {
                    errorLabel.setText("<html>*Should contain <br>First and Surname</html>");
                    nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
                    return false;

                }
            }
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 10));
            errorLabel.setText("");
            return true;
        } else {
            errorLabel.setText("*Name is required");
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 135, 0, 0));
            return false;
        }

    }

    public static boolean dobValidate(JLabel dateOfBirthLabel, JTextField dobField, JLabel errorLabel) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (dobField.getText().length() > 0) {
            try {
                dateFormat.parse(dobField.getText());
                errorLabel.setText("");
                dateOfBirthLabel.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 10));
                return true;
            } catch (ParseException e4) {

                errorLabel.setText("*Invalid Date format");
                dateOfBirthLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
                return false;
            }
        } else {
            errorLabel.setText("<html>*Date of birth<br> is required</html>");
            dateOfBirthLabel.setBorder(BorderFactory.createEmptyBorder(0, 110, 0, 0));
            return false;
        }
    }

    public static boolean mobileNumberValidate(JLabel mobileNumberLabel, JTextField mobileNumberField,
            JLabel errorLabel) {
        if (mobileNumberField.getText().length() > 0) {
            for (char chr : mobileNumberField.getText().toCharArray()) {
                if (!Character.isDigit(chr)) {
                    errorLabel.setText("<html>*Should contain<br> only digits</html>");
                    mobileNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
                    return false;
                }
            }
            errorLabel.setText("");
            mobileNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
            return true;
        } else {
            errorLabel.setText("<html>*Mobile Number<br> is required</html>");
            mobileNumberLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
            return false;
        }
    }

    public static boolean idValidate(JLabel iDLabel, JTextField idField, JLabel idErrorLabel) {
        if (idField.getText().length() > 0) {
            idErrorLabel.setText("");
            iDLabel.setBorder(BorderFactory.createEmptyBorder(0, 45, 0, 10));
            return true;
        } else
            idErrorLabel.setText("*ID is required");
        iDLabel.setBorder(BorderFactory.createEmptyBorder(0, 115, 0, 0));
        return false;
    }

    public static boolean FileNavigation(File[] files, JFrame frame) {

        for (File file : files) {

            if (file.isFile()) {

                String fileName = file.getName();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {

                } else {
                    JOptionPane.showMessageDialog(frame, "Only jpg or png files are allowed", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Select an appropriate file", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }
        return true;

    }

    public static void summary(Consultation consultation, String doctor_name, Patient patient, File file,
            ArrayList<File> notes, JFrame f) {

        Font poppin = Font.getFont(Font.MONOSPACED);
        Font sansbald = Font.getFont(Font.MONOSPACED);
        try {
            poppin = Font.createFont(Font.TRUETYPE_FONT, new File("font/poppins.otf"));
            sansbald = Font.createFont(Font.TRUETYPE_FONT, new File("font/SansBland.ttf"));
        } catch (FontFormatException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JFrame frame = new JFrame();
        frame.setTitle("Consultation Summary");
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());

        JPanel header = new JPanel();

        JLabel header_label = new JLabel("Consultation Details", SwingConstants.CENTER);
        header_label.setFont(poppin.deriveFont(22f));
        header_label.setForeground(Color.decode("#ffffff"));

        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        header.setBackground(Color.decode("#0ccff"));
        header.add(header_label);

        JPanel container = new JPanel(new GridLayout(3, 1));
        JPanel consultation_details = new JPanel(new GridLayout(3, 1));
        JPanel doctor_panel = new JPanel(new FlowLayout());
        doctor_panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        JLabel doctor = new JLabel("Dr. ".concat(doctor_name));
        doctor.setFont(poppin.deriveFont(18f));
        doctor_panel.add(doctor, SwingConstants.CENTER);
        consultation_details.add(doctor_panel);

        String expertise = "";
        JPanel doctor_specialization = new JPanel(new FlowLayout());
        doctor_specialization.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
        for (Doctor doc : SkinConsultationManager.doc) {
            if ((doc.doctor_fname() + " " + doc.doctor_surname()).equals(doctor_name)) {
                expertise = doc.doctor_specialisation();
                break;
            }
        }

        String specialization = String.format("(Specialized in %s)", expertise);
        JLabel specializationLabel = new JLabel(specialization);
        specializationLabel.setFont(sansbald.deriveFont(16f));

        doctor_specialization.add(specializationLabel);
        consultation_details.add(doctor_specialization);

        JPanel appointment_date = new JPanel(new FlowLayout());
        String date = String.format("Scheduled on %s at %s", consultation.consultation_date(),
                consultation.consultation_time());
        JLabel datelabel = new JLabel(date);
        datelabel.setFont(sansbald.deriveFont(17f));
        consultation_details.add(appointment_date);

        appointment_date.add(datelabel, SwingConstants.CENTER);
        JPanel flowLayout = new JPanel(new FlowLayout());
        JPanel patient_details = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel[] criteria = {
                new JLabel(
                        String.format("<html>Consultancy fee: &ensp;£%.2f per hour </html>",
                                consultation.consultation_cost())),
                new JLabel(String.format("<html>Notes: &ensp;%s</html>", consultation.consultation_notes())),
                new JLabel(String.format("<html>ID Number: &ensp;%s</html>", patient.patient_id())),
                new JLabel(String.format("<html>Mobile Number: &ensp;%s</html>", patient.person_mob())),
                new JLabel(String.format("<html>Date of Birth: &ensp;%s</html>", patient.person_dob())),
                new JLabel(String.format("<html>Full Name:&ensp;%s %s</html>", patient.person_name(),
                        patient.person_surname()))
        };
        for (JLabel jLabel : criteria) {
            jLabel.setFont(sansbald.deriveFont(16f));
            patient_details.add(jLabel, SwingConstants.CENTER);
        }
        flowLayout.add(patient_details);
        flowLayout.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));

        JPanel btn = new JPanel(new FlowLayout());
        btn.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        JButton[] options = { new JButton("Edit"), new JButton("Save") };

        for (JButton jButton : options) {
            btn.add(jButton);
            jButton.setPreferredSize(new Dimension(100, 30));
            jButton.setFont(sansbald.deriveFont(16f));
            jButton.setBackground(Color.decode("#00ee00"));
            jButton.setForeground(Color.decode("#ffffff"));
        }

        options[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                f.setVisible(true);
            }
        });
        options[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileOutputStream fos;

                try {
                    fos = new FileOutputStream(file, true);
                    String format = String.format("%s\n%s %s\n%s %s %s %s %s\n", doctor_name,
                            consultation.consultation_date(), consultation.consultation_time(), patient.person_name(),
                            patient.person_surname(), patient.person_dob(), patient.person_mob(), patient.patient_id());
                    fos.write(format.getBytes());
                    notesEncryptions(consultation.consultation_notes(), notes);
                    frame.dispose();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });

        container.add(btn, SwingConstants.CENTER);
        container.add(flowLayout, SwingConstants.CENTER);
        container.add(consultation_details, SwingConstants.CENTER);
        container.setBackground(Color.gray);
        frame.add(container, BorderLayout.CENTER);
        frame.add(header, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void notesEncryptions(String notes, ArrayList<File> files) {

        byte[] data_txt = notes.getBytes();

        try {

            String encryptedData = "Encrypted.txt";
            KeyGenerator public_key = KeyGenerator.getInstance("AES");
            public_key.init(128);
            SecretKey private_key = public_key.generateKey();

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, private_key);

            byte[] notes_txt = cipher.doFinal(data_txt);
            byte[][] notes_img = new byte[files.size()][];
            FileOutputStream fos = new FileOutputStream(encryptedData, true);
            fos.write(notes_txt);
            System.out.println(notes_txt);
            for (int i = 0; i < files.size(); i++) {
                notes_img[i] = cipher.doFinal(files.get(i).getAbsolutePath().getBytes());
                fos.write(notes_img[i]);
            }
            fos.write("\n".getBytes());

        } catch (Exception e) {
        }
    }

}

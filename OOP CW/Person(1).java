import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Consists of the parent Person class
 */
public class Person {

    private String name;
    private String surname;
    private String date_of_birth;
    private String mob_no;

    public Person(String n, String sn, String dob, String mobno) {
        this.name = n;
        this.surname = sn;
        this.date_of_birth = dob;
        this.mob_no = mobno;
    }

    public String person_name() {
        return name;
    }

    public String person_surname() {
        return this.surname;
    }

    public String person_dob() {
        return this.date_of_birth;
    }

    public String person_mob() {
        return this.mob_no;
    }

    public void set_name(String n) {
        this.name = n;
    }

    public void set_surname(String sn) {
        this.surname = sn;
    }

    public void set_dob(String dob) {
        this.date_of_birth = dob;
    }

    public void set_mob(String mobno) {
        this.mob_no = mobno;
    }
}

/**
 * Doctor inherits attributes from Person
 */
class Doctor extends Person {
    private Integer medical_id;
    private String specialisation;

    public Doctor(String n, String sn, String dob, String mobno, Integer ID, String specialisation) {
        super(n, sn, dob, mobno);
        this.medical_id = ID;
        this.specialisation = specialisation;
    }

    public int doctor_id() {
        return this.medical_id;
    }

    public String doctor_specialisation() {
        return this.specialisation;
    }

    public String doctor_fname() {
        return super.person_name();
    }

    public String doctor_surname() {
        return super.person_surname();
    }

    public String doctor_mob() {
        return super.person_mob();

    }

    public String doctor_details() {
        return (String.format(
                "Name: %s %s\nDate of birth: %s \nContact no: %s\nMedical license no: %04d\nSpecialized field: %s\n",
                super.person_name(), super.person_surname(), super.person_dob(),
                super.person_mob(), this.medical_id, this.specialisation));
    }

    public String save_details() {
        return (String.format("%s %s %s %s %04d %s", super.person_name(), super.person_surname(), super.person_dob(),
                super.person_mob(), this.medical_id, this.specialisation));
    }

    public void set_doctorID(Integer ID) {
        this.medical_id = ID;
    }

    public void set_doctorSpec(String specialisation) {
        this.specialisation = specialisation;
    }
}

/**
 * Patient inheritsattributes from Person class
 */
class Patient extends Person {

    private String patient_id;

    public Patient(String n, String sn, String dob, String mobno, String ID) {
        super(n, sn, dob, mobno);
        this.patient_id = ID;
    }

    public String patient_id() {
        return this.patient_id;
    }

    public String patient_details() {
        return String.format("%s %s %s %s %s ", super.person_name(), super.person_surname(), super.person_dob(),
                super.person_mob(), this.patient_id);
    }

}

/**
 * Consultation class for creating new appointments
 */
class Consultation {

    private String date;
    private String time;
    private Double cost;
    private String notes;

    public Consultation(String date, String time, Double cost, String notes) {

        this.date = date;
        this.time = time;
        this.cost = cost;
        this.notes = notes;

    }

    public String consultation_details() {
        return String.format("%s %s %.2f\n", date, time, cost);
    }

    public String consultation_notes() {
        return notes;
    }

    public String consultation_date() {
        return this.date;
    }

    public String consultation_time() {
        return this.time;
    }

    public Double consultation_cost() {
        return this.cost;
    }

}

/**
 * SkinConsultationManager interface implementation
 */
interface SkinConsultationManager {

    // Declaration of an Doctor array list consisting initial of 10 capacity
    static ArrayList<Doctor> doc = new ArrayList<>(10);

    public static void addDoctor(Scanner sc) {

        // Requesting relevant information of Doctor and validating to compact
        System.out.println("\n\nPlease enter the details as per followed:\n");

        System.out.println("- First Name of the Doctor(exclude spaces):\n");
        String fn = sc.nextLine();

        System.out.println("\n- Enter Surname of the Doctor(exclude spaces):\n");
        String sn = sc.nextLine();

        while (true) {
            if (fn.matches("[a-zA-Z]+") && sn.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.println("\nPlease re-enter First Name and Surname:\n");
            String[] name = sc.nextLine().split(" ");
            if (name.length == 2) {
                fn = name[0];
                sn = name[1];
            }
        }

        System.out.println("\n\n- Enter date of birth (in the format dd-mm-yyyy):\n");
        SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy"); // Abides a pattern or threshold to format date
        String date;
        while (true) {
            try {
                date = d.format(d.parse(sc.nextLine()));
                break;
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                System.out.println("\nEnter the date in valid format:\n");
            }
        }

        System.out.println("\n\n- Enter the mobile number(e.g.- 07xxxxxxx):\n");
        String mb = sc.nextLine();
        while (true) {
            if (mb.matches("\\d+")) {
                if (mb.substring(0, 2).equals("07"))
                    break;
            }
            System.out.println("\nPlease re-enter the mobile number(e.g.- 07xxxxxxx):\n");
            mb = sc.nextLine();
        }

        System.out.println("\n\n- Enter Doctor ID:\n");
        Integer id = sc.nextInt();

        // Ensuring replication of ID doesn't exist
        for (Doctor doctor : doc) {
            if (id == doctor.doctor_id()) {
                while (true) {
                    System.out.println(
                            "\n- Duplicating ID, ID pre-exists in records ! \n  Re-enter the medical license ID:\n");
                    id = sc.nextInt();
                    if (id != doctor.doctor_id())
                        break;
                }
            }
        }

        System.out.println("\n\n- Enter the field of specialisation:\n");
        String specialisation = sc.next();

        System.out.println("\n\nSucessfully added a Doctor to the centre\n\n");
        doc.add(new Doctor(fn, sn, date, mb, id, specialisation));
    };

    public static void deleteDoctor(Scanner sc) {
        if (!doc.isEmpty()) {

            System.out.println("\n\nPlease enter the medical license number:\n");
            int id = Integer.parseInt(sc.nextLine());

            // Deleting doctor based on the unique identifier
            for (Doctor doctor : doc) {
                if (doctor.doctor_id() == id) {
                    doc.remove(doctor);
                    System.out.println(String.format(
                            "\n\nSuccessfully deleted records of the doctor\nNumber of doctors residue: %d\n",
                            doc.size()));
                    return;
                }
            }

        } else {
            System.out.println("\n\n*Please appoint a doctor to the centre prior removing*\n\n");
            return;
        }
        System.out.println("\n\n*License ID doesn't exist in records*\n\n");

    };

    public static void printDoctor() {
        System.out.println();

        // Assure doctor field is available on preprocessing
        if (!doc.isEmpty()) {
            Collections.sort(doc, (a, b) -> a.doctor_surname().compareTo(b.doctor_surname()));
            for (Doctor doctor : doc) {
                System.out.println(doctor.doctor_details());
            }
            System.out.println();
        } else {
            System.out.println("\n\n*Please appoint a doctor to the centre prior display info*\n\n");
        }
    };

    public static void saveInfo() {

        try {
            File fileObj = new File("Information.txt");

            // Retrieving absolute location to access the local file and overwrite
            System.out.println("\n\nOverwriting\n\n");
            FileWriter save = new FileWriter(fileObj.getAbsolutePath());
            for (Doctor doctor : doc) {
                save.write(doctor.save_details() + "\n");
            }
            System.out.println("\nSuccessfully saved information\n");
            save.close();

        } catch (IOException e) {
            System.out.println("\n\n*An error has occurred while saving file*\n\n");
        }

    }

    // Augment the program with the appropriate information in load
    public static void loadInfo(Scanner sc) {
        String file = "C:\\Users\\Dell\\OneDrive\\Desktop\\Java\\OOP CW\\Information.txt";
        File fileObj = new File(file);
        try {
            Scanner read = new Scanner(fileObj);
            try {

                while (read.hasNextLine()) {

                    doc.add(new Doctor(read.next(), read.next(), read.next(), read.next(),
                            Integer.parseInt(read.next()), read.next()));
                }
            } catch (NoSuchElementException e) {
            }
        } catch (FileNotFoundException e) {
            return;
        }
    }

}

/**
 * Implementing the SkinConsutant interface
 */
class WestminsterSkinConsultationManager implements SkinConsultationManager {

    public static void main(String[] args) {

        int option = 0;
        Scanner sc = new Scanner(System.in);
        SkinConsultationManager.loadInfo(sc); // Loads information prior entering the program

        do {
            System.out.println("\n");
            for (int i = 0; i < 100; i++) {

                System.out.print("#");
            }
            System.out.println();
            System.out.println("\nPlease select option from the following:\n\n1 - Add a new doctor to the system");
            System.out.println("2 - Delete a doctor from the system\n3 - Display list of doctor");
            System.out.println("4 - Save details\n5 - Display Patient GUI\n6 - Quit the thread\n");

            // Validating and selecting an option for operation
            try {
                option = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\n\n*Please select a valid option*\n");
            }

            switch (option) {

                // Optimizing by eliminating the declaration of another scanner object
                case 1:
                    if (doc.size() != 10) {
                        SkinConsultationManager.addDoctor(sc);
                        sc.nextLine();
                    } else {
                        System.out
                                .println("\nUnable to allocate more doctors to the centre. Maximum capacity reached\n");
                    }
                    break;

                case 2:
                    SkinConsultationManager.deleteDoctor(sc);
                    break;

                case 3:
                    SkinConsultationManager.printDoctor();
                    break;

                case 4:
                    SkinConsultationManager.saveInfo();
                    break;

                case 5:
                    GUI.display();
                    break;

                // Exits program
                case 6:
                    System.out.println("\nAppreciate your consideration, see you later.\n\n");

                    for (int i = 0; i < 100; i++) {
                        System.out.print("#");
                    }
                    System.exit(0);

            }

        } while (option != 6);
    }

}

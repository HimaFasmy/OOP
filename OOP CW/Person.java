import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.DoubleToLongFunction;

/**
 * Person
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
}

/**
 * Doctor
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

    public void update_doctorID(Integer ID) {
        this.medical_id = ID;
    }

    public void update_doctorSpec(String specialisation) {
        this.specialisation = specialisation;
    }
}

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
 * SkinConsultationManager
 */
interface SkinConsultationManager {
    static ArrayList<Doctor> doc = new ArrayList<>(10);

    public static void addDoctor(Scanner sc) {
        System.out.println("\nPlease enter the details as per followed:\n");
        System.out.println("- First Name of the Doctor:");
        String fn = sc.nextLine();

        System.out.println("\n- Enter Surname of the Doctor:");
        String sn = sc.nextLine();

        System.out.println("\n- Enter date of birth (in the format dd-mm-yyyy):");
        SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
        String date;

        while (true) {
            try {
                date = d.format(d.parse(sc.nextLine()));
                break;
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                System.out.println("\nEnter the date in valid format:");
            }
        }

        System.out.println("\n- Enter the mobile number:");
        String mb = sc.nextLine();

        System.out.println("\n- Enter Doctor ID:");
        Integer id = sc.nextInt();

        for (Doctor doctor : doc) {
            if (id == doctor.doctor_id()) {
                while (true) {
                    System.out.println(
                            "\n- Duplicating ID, ID pre-exists in records ! \n  Re-enter the medical license ID:");
                    id = sc.nextInt();
                    if (id != doctor.doctor_id())
                        break;
                }
            }
        }

        System.out.println("\n- Enter the field of specialisation:");
        String specialisation = sc.next();

        doc.add(new Doctor(fn, sn, date, mb, id, specialisation));
    };

    public static void deleteDoctor(Scanner sc) {
        if (!doc.isEmpty()) {

            System.out.println("\nPlease enter the medical license number:");
            int id = Integer.parseInt(sc.nextLine());

            for (Doctor doctor : doc) {
                if (doctor.doctor_id() == id) {
                    doc.remove(doctor);
                    System.out.println("\nSuccessfully deleted records of the doctor\n");
                    return;
                }
            }
        } else {
            System.out.println("\nPlease appoint a doctor to the centre prior removing.\n");
            return;
        }
        System.out.println("\nLicense ID doesn't exist in records\n");
    };

    public static void printDoctor() {
        System.out.println();
        if (!doc.isEmpty()) {
            Collections.sort(doc, (a, b) -> a.doctor_surname().compareTo(b.doctor_surname()));
            for (Doctor doctor : doc) {
                System.out.println(doctor.doctor_details());
            }
            System.out.println();
        } else {
            System.out.println("\nPlease appoint a doctor to the centre prior display info\n");
        }
    };

    public static void saveInfo() {

        try {
            File fileObj = new File("Information.txt");

            System.out.println("\nOverwriting\n");
            FileWriter save = new FileWriter(fileObj.getAbsolutePath());
            for (Doctor doctor : doc) {
                save.write(doctor.save_details() + "\n");
            }
            System.out.println("\nSuccessfully saved information\n");
            save.close();

        } catch (IOException e) {
            System.out.println("\nAn error has occurred while saving file.\n");
        }

    }

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
 * WestminsterSkinConsultationManagerv
 */
class WestminsterSkinConsultationManager implements SkinConsultationManager {

    public static void main(String[] args) {

        int option;
        Scanner sc = new Scanner(System.in);
        SkinConsultationManager.loadInfo(sc);
        GUI.display();
        do {

            System.out.println("\nPlease select option from the following:\n\n1 - Add a new doctor to the system");
            System.out.println("2 - Delete a doctor from the system\n3 - Display list of doctor");
            System.out.println("4 - Save details\n5 - Quit the thread\n");

            option = Integer.parseInt(sc.nextLine());

            switch (option) {

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

            }

        } while (option != 5);
        System.out.println("\nAppreciate your consideration, see you later.\n");

    }

}

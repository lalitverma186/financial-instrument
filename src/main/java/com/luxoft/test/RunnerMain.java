package com.luxoft.test;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.opencsv.CSVReader;

@SuppressWarnings("unchecked")
public class RunnerMain {

    static double instrument1TotalValue, instrument2NovemberValue, instrument3RangeValue = 0;

    static int instrument1TotalCount, instrument2NovemberCount, instrument3RangeCount = 0;

    static double Instrument1Mean, Instrument2Mean, Instrument3Mean;

    public static void main(String[] args) throws Exception {
        FileReader fileReaderObj = new FileReader("C:\\Myfolder\\final.txt");
        CSVReader instrumentReader = new CSVReader(fileReaderObj);
        List<String[]> allInstrumentsList = instrumentReader.readAll();
        // Looping List and passing them to Calculation Engine.
        for (String[] row : allInstrumentsList) {
            calculationEngine(row);
        }
        System.out.println("Instrument1Mean Value--> " + Instrument1Mean);
        System.out.println("Instrument2Mean Value--> " + Instrument2Mean);
        System.out.println("Instrument3Mean Value--> " + Instrument3Mean);

        //Save to h2 database
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
        try {
            persist(sessionFactory);
            load(sessionFactory);
        } finally {
            sessionFactory.close();
        }
    }

    private static void calculationEngine(String[] row) {
        for (String data : row) {
            if (data.equals("INSTRUMENT1")) {
                instrument1TotalValue += Double.parseDouble(row[2]);
                instrument1TotalCount = instrument1TotalCount + 1;
                break;
            } else if (data.equals("INSTRUMENT2")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(row[1], formatter);
                if (date.getYear() == 2014 && date.getMonth().toString() == "NOVEMBER") {
                    instrument2NovemberValue += Double.parseDouble(row[2]);
                    instrument2NovemberCount = instrument2NovemberCount + 1;
                }
                break;
            }
            //Assuming average price of Jan2011 till Dec2013
            else if (data.equals("INSTRUMENT3")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(row[1], formatter);
                if (date.getYear() > 2010 && date.getYear() < 2014) {
                    instrument3RangeValue += Double.parseDouble(row[2]);
                    instrument3RangeCount = instrument3RangeCount + 1;
                }
                break;
            } else {
                System.out.println("###############################");
                //Todo: For any other instrument from the input file - sum of the newest 10 elements (in terms of the date).
            }
        }
        Instrument1Mean = instrument1TotalValue / instrument1TotalCount;
        Instrument2Mean = instrument2NovemberValue / instrument2NovemberCount;
        Instrument3Mean = instrument3RangeValue / instrument3RangeCount;
    }

    public static void load(SessionFactory sessionFactory) {
        System.out.println("-- loading instruments --");
        Session session = sessionFactory.openSession();
        List<InstrumentPriceModifier> instruments = session
                .createQuery("FROM InstrumentPriceModifier where NAME!=null")
                .list();
        instruments.forEach((x) -> System.out.printf("- %s%n", x));
        session.close();
    }

    public static void persist(SessionFactory sessionFactory) {
        InstrumentPriceModifier p1 = new InstrumentPriceModifier("INSTRUMENT1", 2);
        InstrumentPriceModifier p2 = new InstrumentPriceModifier("INSTRUMENT2", 2);
        InstrumentPriceModifier p3 = new InstrumentPriceModifier("INSTRUMENT3", 2);
        System.out.println("-- persisting instruments --");
        System.out.printf("- %s%n- %s%n- %s%n", p1, p2, p3);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(p1);
        session.save(p2);
        session.save(p3);
        session.getTransaction().commit();
    }
}

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

    private static final String INSTRUMENT1 = "INSTRUMENT1";

    private static final String INSTRUMENT2 = "INSTRUMENT2";

    private static final String INSTRUMENT3 = "INSTRUMENT3";

    private static final String JUNK_DATA_1 = "JUNK_DATA-1";

    private static final String JUNK_DATA_2 = "JUNK_DATA-2";

    static double instrument1TotalValue, instrument2NovemberValue, instrument3RangeValue = 0;

    static int instrument1TotalCount, instrument2NovemberCount, instrument3RangeCount = 0;

    static double Instrument1Mean, Instrument2Mean, Instrument3Mean;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        //Reading the file and making the list
        FileReader fileReaderObj = new FileReader("C:\\Myfolder\\final.txt");
        CSVReader instrumentReader = new CSVReader(fileReaderObj);
        List<String[]> allInstrumentsList = instrumentReader.readAll();

        // Looping List and passing them to Calculation Engine.
        for (String[] row : allInstrumentsList) {
            calculationEngine(row);
        }
        //Instruments value after calculation engine
        System.out.println("Instrument1 Value--> " + Instrument1Mean);
        System.out.println("Instrument2 Value--> " + Instrument2Mean);
        System.out.println("Instrument3 Value--> " + Instrument3Mean);
        //Save instruments to database(h2)
        SessionFactory sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
        try {
            persist(sessionFactory);

            //Query and load data from database to find the Final price of the instrument.
            load(sessionFactory);
        } finally {
            sessionFactory.close();
        }
    }

    /**
     * Calculation Engine to calculate the value of the instruments based on business logic.
     *
     * @param row row data.
     */
    private static void calculationEngine(String[] row) {
        for (String data : row) {
            if (data.equals(INSTRUMENT1)) {
                //For INSTRUMENT1 – mean
                instrument1TotalValue += Double.parseDouble(row[2]);
                instrument1TotalCount = instrument1TotalCount + 1;
                break;
            } else if (data.equals(INSTRUMENT2)) {
                //For INSTRUMENT2 – mean for November 2014
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(row[1], formatter);
                if (date.getYear() == 2014 && date.getMonth().toString() == "NOVEMBER") {
                    instrument2NovemberValue += Double.parseDouble(row[2]);
                    instrument2NovemberCount = instrument2NovemberCount + 1;
                }
                break;
            }
            //For INSTRUMENT3 – any other statistical calculation that we can compute "on-the-fly" as we read the file (it's up to you)
            //Assuming mean/average price of Jan2011 till Dec2013
            else if (data.equals(INSTRUMENT3)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                LocalDate date = LocalDate.parse(row[1], formatter);
                if (date.getYear() > 2010 && date.getYear() < 2014) {
                    instrument3RangeValue += Double.parseDouble(row[2]);
                    instrument3RangeCount = instrument3RangeCount + 1;
                }
                break;
            }
        }
        Instrument1Mean = instrument1TotalValue / instrument1TotalCount;
        Instrument2Mean = instrument2NovemberValue / instrument2NovemberCount;
        Instrument3Mean = instrument3RangeValue / instrument3RangeCount;
    }

    private static void load(SessionFactory sessionFactory) {
        System.out.println("-- Loading instruments --");
        Session session = sessionFactory.openSession();
        //query the database to see if there is an entry for the <INSTRUMENT_NAME>;
        List<InstrumentPriceModifier> instruments = session
                .createQuery("FROM InstrumentPriceModifier where name like 'INSTRUMENT%'")
                .list();
        instruments.forEach((x) -> System.out.printf("- %s%n", x));
        //Final price of the Instruments
        double finalPriceInstrument1 = 0;
        double finalPriceInstrument2 = 0;
        double finalPriceInstrument3 = 0;
        for (int i = 0; i < instruments.size(); i++) {
            if (0 != instruments.get(i).getMultiplier()) {
                finalPriceInstrument1 = Instrument1Mean * instruments.get(0).getMultiplier();
                finalPriceInstrument2 = Instrument2Mean * instruments.get(1).getMultiplier();
                finalPriceInstrument3 = Instrument3Mean * instruments.get(2).getMultiplier();
            } else {
                finalPriceInstrument1 = Instrument1Mean;
                finalPriceInstrument2 = Instrument2Mean;
                finalPriceInstrument3 = Instrument3Mean;
            }
        }
        System.out.println("*******************Final Results**********************************");
        System.out.println("Final Price of instrument1 == " + finalPriceInstrument1);
        System.out.println("Final Price of instrument2 == " + finalPriceInstrument2);
        System.out.println("Final Price of instrument3 == " + finalPriceInstrument3);
        session.close();
    }

    private static void persist(SessionFactory sessionFactory) {
        InstrumentPriceModifier p1 = new InstrumentPriceModifier(INSTRUMENT1, 2);
        InstrumentPriceModifier p2 = new InstrumentPriceModifier(INSTRUMENT2, 2);
        InstrumentPriceModifier p3 = new InstrumentPriceModifier(INSTRUMENT3, 2);
        InstrumentPriceModifier p4 = new InstrumentPriceModifier(JUNK_DATA_1, 10);
        InstrumentPriceModifier p5 = new InstrumentPriceModifier(JUNK_DATA_2, 5);
        System.out.println("-- Persisting instruments --");
        System.out.printf("- %s%n- %s%n- %s%n- %s%n- %s%n", p1, p2, p3, p4, p5);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(p1);
        session.save(p2);
        session.save(p3);
        session.save(p4);
        session.save(p5);
        session.getTransaction().commit();
    }
}

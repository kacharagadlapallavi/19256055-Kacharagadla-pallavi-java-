package Q1;

import java.util.*;

/**
 * RailwayTicketGenerator
 * Demonstrates ticket generation using String (+), StringBuilder, and StringBuffer,
 * as well as alternative approaches (StringJoiner, String.format).
 */
public class RailwayTicketGenerator {

    public static class TicketField {
        public final String key;
        public final String value;

        public TicketField(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Generates a sample list of ticket fields of given count n.
     */
    public static List<TicketField> generateTicketFields(int n, int ticketIndex) {
        List<TicketField> fields = new ArrayList<>(n);
        fields.add(new TicketField("PNR", "842918" + String.format("%04d", ticketIndex)));
        fields.add(new TicketField("TRAIN_NO", "12626"));
        fields.add(new TicketField("TRAIN_NAME", "KERALA EXPRESS"));
        fields.add(new TicketField("FROM_STN", "NDLS - NEW DELHI"));
        fields.add(new TicketField("TO_STN", "TVC - TRIVANDRUM CENTRAL"));
        fields.add(new TicketField("DATE_OF_JOURNEY", "2026-12-25"));
        fields.add(new TicketField("CLASS", "3A"));
        fields.add(new TicketField("QUOTA", "GENERAL"));
        fields.add(new TicketField("BASE_FARE", "1850.00"));
        fields.add(new TicketField("TOTAL_FARE", "1942.50"));

        for (int i = 10; i < n; i++) {
            fields.add(new TicketField("FIELD_" + i, "DATA_VAL_" + ticketIndex + "_" + i));
        }
        return fields;
    }

    /**
     * Approach A: Repeated concatenation using '+' operator inside a loop.
     */
    public static String generateWithPlusOperator(List<TicketField> fields) {
        String result = "";
        for (TicketField field : fields) {
            // String immutability forces allocation of a new String and character copy on every iteration
            result = result + "[" + field.key + ":" + field.value + "] ";
        }
        return result;
    }

    /**
     * Approach B: Using StringBuilder (Unsynchronized, mutable char buffer).
     */
    public static String generateWithStringBuilder(List<TicketField> fields) {
        StringBuilder sb = new StringBuilder();
        for (TicketField field : fields) {
            sb.append("[").append(field.key).append(":").append(field.value).append("] ");
        }
        return sb.toString();
    }

    /**
     * Approach C: Using StringBuffer (Synchronized, thread-safe, mutable char buffer).
     */
    public static String generateWithStringBuffer(List<TicketField> fields) {
        StringBuffer sb = new StringBuffer();
        for (TicketField field : fields) {
            sb.append("[").append(field.key).append(":").append(field.value).append("] ");
        }
        return sb.toString();
    }

    /**
     * Alternative 1: Using StringJoiner.
     */
    public static String generateWithStringJoiner(List<TicketField> fields) {
        StringJoiner sj = new StringJoiner(" ", "", " ");
        for (TicketField field : fields) {
            sj.add("[" + field.key + ":" + field.value + "]");
        }
        return sj.toString();
    }

    /**
     * Alternative 2: Using String.format inside loop.
     */
    public static String generateWithStringFormat(List<TicketField> fields) {
        StringBuilder sb = new StringBuilder();
        for (TicketField field : fields) {
            sb.append(String.format("[%s:%s] ", field.key, field.value));
        }
        return sb.toString();
    }
}


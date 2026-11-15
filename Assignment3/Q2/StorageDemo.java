package Q2;

import java.io.IOException;

/**
 * Q2 - User-Defined Exceptions and Propagation in an Online Examination Portal
 * Complete the Code: StorageDemo.java
 * Student: Moksha Dude (192372374)
 */
class ExamException extends Exception {
    ExamException(String message) { super(message); }
    ExamException(String message, Throwable cause) { super(message, cause); }
}

class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Storage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) throw new IOException("Disk quota exceeded");
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // TODO 1: Wrap the IOException in a SubmissionStorageException, keeping the
            //   IOException as its cause
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}

public class StorageDemo {
    public static void main(String[] args) {
        Storage storage = new Storage();
        try {
            storage.save("answer01.pdf", 120);
            storage.save("answer02.pdf", 800);
        } catch (ExamException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }

        System.out.println("\n--- Inquiry Analysis ---");
        System.out.println("If IOException were NOT passed to the SubmissionStorageException constructor,");
        System.out.println("calling e.getCause() would return null, leading to a NullPointerException on .getMessage()");
        System.out.println("and permanently discarding the underlying system error details.");
        System.out.println("Exception chaining preserves low-level root causes while presenting abstraction-appropriate");
        System.out.println("domain exceptions to calling layers.");
    }
}

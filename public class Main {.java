import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DnuTutorConnect {


    public static abstract class User {
        protected final UUID id;
        protected String name;
        protected String email;
        protected String phone;

        public User(String name, String email, String phone) {
            this.id = UUID.randomUUID();
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        public UUID getId() { return id; }

        public abstract Role getRole();

        @Override
        public String toString() {
            return String.format("%s{id=%s,name=%s,email=%s,phone=%s}",
                    getRole(), id.toString(), name, email, phone);
        }
    }

    public enum Role { STUDENT, TUTOR, ADMIN }

    public static class Student extends User {
        private final List<LearningRequest> requests = new ArrayList<>();
        private final List<Booking> bookings = new ArrayList<>();
        public Student(String name, String email, String phone) { super(name,email,phone); }
        @Override public Role getRole() { return Role.STUDENT; }
        public List<LearningRequest> getRequests() { return requests; }
        public List<Booking> getBookings() { return bookings; }
    }

    public static class Tutor extends User {
        private final Set<Subject> subjects = new HashSet<>();
        private final List<Booking> bookings = new ArrayList<>();
        private final List<Rating> ratingsReceived = new ArrayList<>();
        private final Map<LocalDate, List<TimeSlot>> availability = new HashMap<>();
        private double feePerHour;
        private String profileDescription;

        public Tutor(String name, String email, String phone, double feePerHour, String profileDescription) {
            super(name,email,phone);
            this.feePerHour = feePerHour;
            this.profileDescription = profileDescription;
        }

        @Override public Role getRole() { return Role.TUTOR; }

        public void addSubject(Subject s) { subjects.add(s); }
        public Set<Subject> getSubjects() { return subjects; }
        public void addAvailability(LocalDate date, TimeSlot slot) {
            availability.computeIfAbsent(date, k->new ArrayList<>()).add(slot);
        }
        public Map<LocalDate, List<TimeSlot>> getAvailability() { return availability; }

        public double getFeePerHour() { return feePerHour; }
        public String getProfileDescription() { return profileDescription; }
        public List<Rating> getRatingsReceived() { return ratingsReceived; }
        public List<Booking> getBookings() { return bookings; }

        public double getAverageRating() {
            if (ratingsReceived.isEmpty()) return 0;
            return ratingsReceived.stream().mapToInt(Rating::getScore).average().orElse(0.0);
        }
    }

    public static class Admin extends User {
        public Admin(String name, String email, String phone) { super(name,email,phone); }
        @Override public Role getRole() { return Role.ADMIN; }
    }

    public static class Subject {
        private final UUID id = UUID.randomUUID();
        private final String name;
        public Subject(String name) { this.name = name; }
        public UUID getId() { return id; }
        public String getName() { return name; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Subject)) return false;
            return this.name.equalsIgnoreCase(((Subject)o).name);
        }
        @Override public int hashCode() { return name.toLowerCase().hashCode(); }
        @Override public String toString() { return name; }
    }

    public static class TimeSlot {
        private final LocalDateTime start;
        private final LocalDateTime end;
        public TimeSlot(LocalDateTime start, LocalDateTime end) {
            if (end.isBefore(start)) throw new IllegalArgumentException("end before start");
            this.start = start; this.end = end;
        }
        public LocalDateTime getStart() { return start; }
        public LocalDateTime getEnd() { return end; }
        @Override public String toString() {
            return String.format("[%s -> %s]", start, end);
        }
    }

    public enum RequestStatus { OPEN, MATCHED, CLOSED }

    public static class LearningRequest {
        private final UUID id = UUID.randomUUID();
        private final Student student;
        private final Subject subject;
        private final String description;
        private final LocalDateTime createdAt = LocalDateTime.now();
        private RequestStatus status = RequestStatus.OPEN;
        public LearningRequest(Student student, Subject subject, String description) {
            this.student = student; this.subject = subject; this.description = description;
        }
        public UUID getId() { return id; }
        public Subject getSubject() { return subject; }
        public Student getStudent() { return student; }
        public RequestStatus getStatus() { return status; }
        public void setStatus(RequestStatus s) { this.status = s; }
        @Override public String toString() {
            return String.format("LR{id=%s, student=%s, subject=%s, status=%s}", id, student.name, subject.name, status);
        }
    }

    public enum BookingStatus { PENDING, CONFIRMED, DONE, CANCELLED }

    public static class Booking {
        private final UUID id = UUID.randomUUID();
        private final Student student;
        private final Tutor tutor;
        private final Subject subject;
        private final LocalDateTime scheduleTime;
        private final int durationMinutes;
        private BookingStatus status = BookingStatus.PENDING;
        private Transaction transaction; // optional
        public Booking(Student student, Tutor tutor, Subject subject, LocalDateTime scheduleTime, int durationMinutes) {
            this.student = student; this.tutor = tutor; this.subject = subject;
            this.scheduleTime = scheduleTime; this.durationMinutes = durationMinutes;
        }
        public UUID getId() { return id; }
        public void setStatus(BookingStatus s) { this.status = s; }
        public BookingStatus getStatus(){ return status; }
        public void setTransaction(Transaction t) { this.transaction = t; }
        public Transaction getTransaction(){ return transaction; }
        @Override public String toString() {
            return String.format("Booking{id=%s, student=%s, tutor=%s, subject=%s, time=%s, duration=%dmin, status=%s}",
                    id, student.name, tutor.name, subject.name, scheduleTime, durationMinutes, status);
        }
    }

    public static class Rating {
        private final UUID id = UUID.randomUUID();
        private final Student student;
        private final Tutor tutor;
        private final int score; // 1-5
        private final String comment;
        private final LocalDate date = LocalDate.now();
        public Rating(Student student, Tutor tutor, int score, String comment) {
            if (score < 1 || score > 5) throw new IllegalArgumentException("score range 1-5");
            this.student = student; this.tutor = tutor; this.score = score; this.comment = comment;
        }
        public int getScore() { return score; }
        @Override public String toString() {
            return String.format("Rating{id=%s, student=%s, tutor=%s, score=%d, comment=%s, date=%s}",
                    id, student.name, tutor.name, score, comment, date);
        }
    }

    public enum TransactionStatus { PAID, REFUNDED, FAILED }

    public static class Transaction {
        private final UUID id = UUID.randomUUID();
        private final Booking booking;
        private final double amount;
        private final String method;
        private TransactionStatus status;
        private final LocalDateTime createdAt = LocalDateTime.now();

        public Transaction(Booking booking, double amount, String method, TransactionStatus status) {
            this.booking = booking; this.amount = amount; this.method = method; this.status = status;
        }
        @Override public String toString() {
            return String.format("Transaction{id=%s, booking=%s, amount=%.2f, method=%s, status=%s, time=%s}",
                    id, booking.getId(), amount, method, status, createdAt);
        }
    }

    public static class DnuService {
        private final Map<UUID, User> users = new HashMap<>();
        private final Map<UUID, Subject> subjects = new HashMap<>();
        private final Map<UUID, LearningRequest> requests = new HashMap<>();
        private final Map<UUID, Booking> bookings = new HashMap<>();
        private final List<Transaction> transactions = new ArrayList<>();

        // Register users
        public Student registerStudent(String name, String email, String phone) {
            Student s = new Student(name,email,phone);
            users.put(s.getId(), s);
            return s;
        }
        public Tutor registerTutor(String name, String email, String phone, double feePerHour, String profile) {
            Tutor t = new Tutor(name,email,phone,feePerHour,profile);
            users.put(t.getId(), t);
            return t;
        }
        public Admin registerAdmin(String name, String email, String phone) {
            Admin a = new Admin(name,email,phone);
            users.put(a.getId(), a);
            return a;
        }

        // Subjects
        public Subject createOrGetSubject(String name) {
            Optional<Subject> existed = subjects.values().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
            if (existed.isPresent()) return existed.get();
            Subject s = new Subject(name); subjects.put(s.getId(), s); return s;
        }

        // Post request
        public LearningRequest postLearningRequest(Student student, Subject subject, String desc) {
            LearningRequest lr = new LearningRequest(student, subject, desc);
            requests.put(lr.getId(), lr);
            student.getRequests().add(lr);
            return lr;
        }

        // Search tutors by subject
        public List<Tutor> searchTutorsBySubject(Subject subject, Double maxFee, Double minRating) {
            return users.values().stream()
                    .filter(u -> u instanceof Tutor)
                    .map(u -> (Tutor)u)
                    .filter(t -> t.getSubjects().contains(subject))
                    .filter(t -> maxFee == null || t.getFeePerHour() <= maxFee)
                    .filter(t -> minRating == null || t.getAverageRating() >= minRating)
                    .sorted(Comparator.comparingDouble(Tutor::getAverageRating).reversed())
                    .collect(Collectors.toList());
        }

        // Create booking
        public Booking createBooking(Student s, Tutor t, Subject subj, LocalDateTime time, int durationMinutes) {
            Booking b = new Booking(s,t,subj,time,durationMinutes);
            bookings.put(b.getId(), b);
            s.getBookings().add(b);
            t.getBookings().add(b);
            return b;
        }

        // Confirm booking (tutor or admin)
        public void confirmBooking(Booking b) { b.setStatus(BookingStatus.CONFIRMED); }

        // Mark done
        public void markBookingDone(Booking b) { b.setStatus(BookingStatus.DONE); }

        // Cancel
        public void cancelBooking(Booking b) { b.setStatus(BookingStatus.CANCELLED); }

        // Add rating
        public Rating addRating(Student s, Tutor t, int score, String comment) {
            Rating r = new Rating(s,t,score,comment);
            t.getRatingsReceived().add(r);
            return r;
        }

        // Create transaction (simulate immediate success)
        public Transaction createTransaction(Booking b, double amount, String method) {
            Transaction tx = new Transaction(b, amount, method, TransactionStatus.PAID);
            transactions.add(tx);
            b.setTransaction(tx);
            return tx;
        }

        // Admin operations
        public List<User> listAllUsers() { return new ArrayList<>(users.values()); }
        public List<Booking> listAllBookings() { return new ArrayList<>(bookings.values()); }
        public List<Transaction> listAllTransactions() { return new ArrayList<>(transactions); }

        /* Utility for demo */
        public Optional<User> findUserByEmail(String email) {
            return users.values().stream().filter(u->u.email.equalsIgnoreCase(email)).findFirst();
        }
    }

    /* =======================
       Demo / Main
       ======================= */

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== DNU Tutor Connect - Demo run ===");
        DnuService service = new DnuService();

        // Create subjects
        Subject math = service.createOrGetSubject("Toan");
        Subject java = service.createOrGetSubject("Lập trinh Java");
        Subject english = service.createOrGetSubject("Tieng Anh");

        // Register users
        Student alice = service.registerStudent("Nguyen Minh A", "alice@dnu.edu.vn", "0901000100");
        Tutor bob = service.registerTutor("Tran Van B", "bob@dnu.edu.vn", "0902000200", 150000.0, "Senior student - Ky thuat phan mem");
        Tutor carol = service.registerTutor("Le Thi C", "carol@dnu.edu.vn", "0903000300", 100000.0, "Giang vien thinh giang - TOEIC coach");
        Admin admin = service.registerAdmin("Admin DNU", "admin@dnu.edu.vn", "0904000400");

        // Tutors add subjects and availability
        bob.addSubject(math); bob.addSubject(java);
        carol.addSubject(english);

        //availability
        bob.addAvailability(LocalDate.now().plusDays(1), new TimeSlot(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0), LocalDateTime.now().plusDays(1).withHour(20).withMinute(0)));

        // Student posts a learning request
        LearningRequest lr1 = service.postLearningRequest(alice, java, "Can hoc Java OOP de lam do an. Uu tien tutor co kinh nghiem thuc te.");

        // Search tutors for Java
        List<Tutor> found = service.searchTutorsBySubject(java, null, null);
        System.out.println("\n--- Tutors found for " + java.getName() + " ---");
        found.forEach(t -> System.out.println(t.name + " | Fee: " + t.getFeePerHour() + " | AvgRating: " + t.getAverageRating()));

        // Create a booking: alice với bob
        LocalDateTime lessonTime = LocalDateTime.now().plusDays(1).withHour(18).withMinute(30);
        Booking booking = service.createBooking(alice, bob, java, lessonTime, 90);
        System.out.println("\nCreated booking: " + booking);

        // Student pays (simulate)
        double amount = bob.getFeePerHour() * (booking.durationMinutes / 60.0);
        Transaction tx = service.createTransaction(booking, amount, "Thanh toan qua vi noi bo");
        System.out.println("Transaction created: " + tx);

        // Tutor confirms booking
        service.confirmBooking(booking);
        System.out.println("Booking after confirm: " + booking);

        // After lesson -> mark done
        service.markBookingDone(booking);
        System.out.println("Booking after done: " + booking);

        // Student rates tutor
        Rating rating = service.addRating(alice, bob, 5, "Rat nhiet tinh, giai thich ro rang!");
        System.out.println("New rating: " + rating);
        System.out.println("Tutor average rating now: " + bob.getAverageRating());

        // Admin lists transactions
        System.out.println("\n--- Admin View: Transactions ---");
        service.listAllTransactions().forEach(System.out::println);

        // Show all users
        System.out.println("\n--- List of all users ---");
        service.listAllUsers().forEach(System.out::println);

        System.out.println("\n=== Demo finished ===");
    }
}


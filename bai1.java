public class Bai1 {
    private String maTaiKhoan;
    private double soTien;

    public Bai1(String maTaiKhoan, double soTien) {
        this.maTaiKhoan = maTaiKhoan;
        this.soTien = soTien;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public static void main(String[] args) {
        Bai1 acc = new Bai1("TK001", 500000);

        System.out.println("Mã TK: " + acc.getMaTaiKhoan() + ", Số tiền: " + acc.getSoTien());

        acc.setSoTien(1000000);

        System.out.println("Mã TK: " + acc.getMaTaiKhoan() + ", Số tiền mới: " + acc.getSoTien());
    }
}

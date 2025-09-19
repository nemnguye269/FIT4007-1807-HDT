import java.time.Year;

public class Bai3 {
    private String maSV;
    private String hoTen;
    private int namSinh;
    private String diaChi;

    public Bai3(String maSV, String hoTen) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.namSinh = 0;
        this.diaChi = "";
    }

    public Bai3(String maSV, String hoTen, int namSinh, String diaChi) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.diaChi = diaChi;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(int namSinh) {
        this.namSinh = namSinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int tinhTuoi() {
        int namHienTai = Year.now().getValue();
        return namHienTai - namSinh;
    }

    public static void main(String[] args) {
        Bai3 sv = new Bai3("SV001", "Nguyen Van A", 2003, "Ha Noi");

        System.out.println("Mã SV: " + sv.getMaSV());
        System.out.println("Họ tên: " + sv.getHoTen());
        System.out.println("Năm sinh: " + sv.getNamSinh());
        System.out.println("Địa chỉ: " + sv.getDiaChi());
        System.out.println("Tuổi: " + sv.tinhTuoi());
    }
}

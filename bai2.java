public class bai2 {
    private String maSach;
    private String tenSach;
    private double giaSach;
    private double giamGia; 

    
    public bai2(String maSach, String tenSach) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.giaSach = 0;
        this.giamGia = 0;
    }

    public bai2(String maSach, String tenSach, double giaSach, double giamGia) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.giaSach = giaSach;
        this.giamGia = giamGia;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public double getGiaSach() {
        return giaSach;
    }

    public void setGiaSach(double giaSach) {
        this.giaSach = giaSach;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getGiaBan() {
        return giaSach - giamGia;
    }
}

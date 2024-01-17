package ParentClasses;

public class Transponder {
    //jakiś sygnał?
    enum POLARIZATION {
        H,
        V,
        L,
        R
    }

    String name; // (Like "2C")
    int frequency;
    POLARIZATION polarization;
    String standard; // (Like "DVB-S2")
    String encoding; // (Like "16APSK")
    int SR;
    String FEC; // Should it be string? Some of them are like "3/4" ad some are just "Auto"
}

public class PredicateMain {
    public static void main(String[] args) {
       printInfo(str->{
           System.out.println("111----->"+str);
            return  str >10;
        },str->{
            System.out.println("222----->"+str);
            return  str>100;});
    }

    public static void printInfo ( PredicateTest con1, PredicateTest con2){

           System.out.println(con1.and(con2).test(11));
    }
}

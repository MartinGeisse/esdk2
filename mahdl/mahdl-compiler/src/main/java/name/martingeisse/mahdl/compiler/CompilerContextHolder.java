package name.martingeisse.mahdl.compiler;

public class CompilerContextHolder {

    static private final ThreadLocal<CompilerContext> holder = new ThreadLocal<>();

    public static void set(CompilerContext context) {
        holder.set(context);
    }

    public static void remove() {
        holder.remove();
    }

    public static CompilerContext get() {
        return holder.get();
    }

}

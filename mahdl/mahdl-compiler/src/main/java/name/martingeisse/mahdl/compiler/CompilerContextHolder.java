package name.martingeisse.mahdl.compiler;

class CompilerContextHolder {

    static private final ThreadLocal<CompilerContext> holder = new ThreadLocal<>();

    static void set(CompilerContext context) {
        holder.set(context);
    }

    static void remove() {
        holder.remove();
    }

    static CompilerContext get() {
        return holder.get();
    }

}

package name.martingeisse.mahdl.input.cm;

public interface SignalLikeDefinition_WithInitializer extends SignalLikeDefinition {

	CmToken getIdentifier();

	ExtendedExpression getInitializer();

}

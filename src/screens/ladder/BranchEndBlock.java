package screens.ladder;
// Arquivo ajustado para não quebrar a compilação no novo sistema
public class BranchEndBlock extends LadderBlock {
    public BranchEndBlock() {
        super("", "BRANCH_END");
    }
    @Override
    public boolean isOutput() { return false; }
    @Override
    public String compileToIL(boolean isFirstElement) { return ""; }
}

package screens.ladder;
// Arquivo ajustado para não quebrar a compilação no novo sistema
public class BranchStartBlock extends LadderBlock {
    public BranchStartBlock() {
        super("", "BRANCH_START");
    }
    @Override
    public boolean isOutput() { return false; }
    @Override
    public String compileToIL(boolean isFirstElement) { return ""; }
}

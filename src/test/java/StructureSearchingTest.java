import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import org.junit.Test;
import sassa.enums.PassType;
import sassa.models.FeatureList_Model;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.models.features.Feature_Registry;
import sassa.searcher.Searching_Thread;
import sassa.util.Result;

import java.util.HashMap;
import java.util.List;

public class StructureSearchingTest {

    @Test
    public void VillageAndDesertPyramidTest() {
        long seed = 4191939634457348L;
        Searching_Thread searching_thread = new Searching_Thread(new Searcher_Model());
        ChunkRand rand = new ChunkRand();
        FeatureList_Model list = new FeatureList_Model();
        list.addFeature(new Feature_Model(Feature_Registry.VILLAGE, 3));
        List<Feature_Model> search = list.getCreatedFeatureListFromVersion(MCVersion.latest());
        Result<PassType, HashMap<Feature_Model, List<CPos>>> pass = searching_thread.featureSearch(search, BPos.ORIGIN, seed, rand);
        assert pass.isSuccessful();
    }
}

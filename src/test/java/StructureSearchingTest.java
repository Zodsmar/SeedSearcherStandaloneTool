import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sassa.enums.PassType;
import sassa.models.FeatureList_Model;
import sassa.models.Feature_Model;
import sassa.models.Searcher_Model;
import sassa.models.features.Feature_Registry;
import sassa.searcher.Searching_Thread;
import sassa.util.BiomeSources;
import sassa.util.Result;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StructureSearchingTest {
    Searcher_Model model = new Searcher_Model();
    Searching_Thread searching_thread = new Searching_Thread(model);
    ChunkRand rand = new ChunkRand();

    @ParameterizedTest
    @ValueSource(longs = {246752403313741L, 132724019605569L, 107331128829414L, 249001086553344L})
    public void StructuresTest(long seed) {

        FeatureList_Model list = new FeatureList_Model();
        list.addFeatures(Arrays.asList(new Feature_Model(Feature_Registry.VILLAGE, 3), new Feature_Model(Feature_Registry.OWRUINEDPORTAL, 2)));
        List<Feature_Model> search = list.getCreatedFeatureListFromVersion(model.getSelectedVersion());
        model.setFeatureList(search);

        //First check that there are even spawns
        Result<PassType, HashMap<Feature_Model, List<CPos>>> pass = searching_thread.featureSearch(search, BPos.ORIGIN, seed, rand);
        assert pass.isSuccessful();


        ///////////// SpawnPoint Checking ////////////////
        //BPos spawnPoint = searching_thread.getSpawnPoint(seed, biomeSources.getOverworldBiomeSource());
        BiomeSources biomeSources = new BiomeSources(seed, model);
        assert searching_thread.featuresCanSpawn(pass.getData(), biomeSources, rand) == true;


    }
}

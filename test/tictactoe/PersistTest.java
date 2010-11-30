package tictactoe;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogPersistedCollection;
import org.junit.Test;


public class PersistTest {
	
	@Test
	public void testPersistence() {
		final EncogPersistedCollection encog = new EncogPersistedCollection("network.eg");
		BasicNetwork network = (BasicNetwork) encog.find("network");
	}
}

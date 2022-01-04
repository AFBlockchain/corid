package hk.edu.polyu.af.bc.account

import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import hk.edu.polyu.af.bc.account.states.TemplateState
import java.util.concurrent.Future;
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import hk.edu.polyu.af.bc.account.flows.Initiator
import net.corda.core.node.services.Vault.StateStatus


class FlowTests {
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode

    @Before
    fun setup() {
        network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.template.contracts"),
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.template.flows")
        )))
        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }
    @Test
    fun `DummyTest`() {
        val flow = Initiator(b.info.legalIdentities[0])
        val future: Future<SignedTransaction> = a.startFlow(flow)
        network.runNetwork()

        //successful query means the state is stored at node b's vault. Flow went through.
        val inputCriteria: QueryCriteria = QueryCriteria.VaultQueryCriteria().withStatus(StateStatus.UNCONSUMED)
        val state = b.services.vaultService.queryBy(TemplateState::class.java, inputCriteria).states[0].state.data
    }
}
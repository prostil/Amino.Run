package amino.run.policy;

import amino.run.kernel.common.GlobalKernelReferences;
import amino.run.kernel.server.KernelServerImpl;
import amino.run.oms.OMSServer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class defines ShiftPolicy which serves the purpose of demonstration only. Note that it does
 * not provide any values; thus, should not be used outside of testing or demonstration. It moves
 * the MicroService object to another MicroService Kernel server when the number of RPC (RMI) is
 * more than 5.
 */
public class ShiftPolicy extends DefaultPolicy {

    public static class ShiftClientPolicy extends DefaultPolicy.DefaultClientPolicy {}

    public static class ShiftServerPolicy extends DefaultPolicy.DefaultServerPolicy {
        private static final Logger logger = Logger.getLogger(ShiftServerPolicy.class.getName());

        private static int LOAD = 5;
        private static int shiftRPCLoad = 0;

        @Override
        public Object onRPC(String method, ArrayList<Object> params) throws Exception {
            this.shiftRPCLoad++;
            Object obj = super.onRPC(method, params);

            if (this.shiftRPCLoad > 0 && this.shiftRPCLoad % this.LOAD == 0) {
                logger.info(
                        "[ShiftPolicy] Limit reached at "
                                + this.LOAD
                                + ". Shift policy triggered.");
                OMSServer oms = GlobalKernelReferences.nodeServer.oms;
                ArrayList<InetSocketAddress> servers =
                        new ArrayList<InetSocketAddress>(oms.getServers(null));

                KernelServerImpl localKernel = GlobalKernelReferences.nodeServer;
                InetSocketAddress localAddress = localKernel.getLocalHost();
                InetSocketAddress shiftWinner = this.getNextShiftTarget(servers, localAddress);

                if (shiftWinner.equals(localAddress)) {
                    logger.info(
                            "[ShiftPolicy] There are no targets to migrate MicroService object to.");
                } else {
                    logger.info(
                            "[ShiftPolicy] Shifting MicroService object "
                                    + this.oid
                                    + " to "
                                    + shiftWinner);
                    localKernel.moveKernelObjectToServer(this, shiftWinner);
                }
            }

            return obj;
        }

        private static InetSocketAddress getNextShiftTarget(
                ArrayList<InetSocketAddress> candidates, InetSocketAddress curr) {
            InetSocketAddress chosen = candidates.get(0);

            Boolean foundCurr = false;
            for (InetSocketAddress node : candidates) {
                if (node.equals(curr)) {
                    foundCurr = true;
                    continue;
                }

                if (foundCurr) {
                    chosen = node;
                    break;
                }
            }

            return chosen;
        }
    }

    public static class ShiftGroupPolicy extends DefaultPolicy.DefaultGroupPolicy {}
}

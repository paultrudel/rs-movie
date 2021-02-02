package rsmovie.prediction;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SVD extends ScorePrediction {

    private static final Logger logger = LoggerFactory.getLogger(SVD.class);

    protected Matrix U;
    protected Matrix Sigma;
    protected Matrix V;
    protected Matrix M;

    public SVD() {
        super();
    }

    protected void computeSVD(Matrix matrix) {
        logger.info("========= Computing SVD ==========");
        SingularValueDecomposition svd = matrix.svd();
        int k = computeNumberOfSingularValuesToKeep(svd);

        U = svd.getU().getMatrix(0, svd.getU().getRowDimension() - 1, 0, k);
        Sigma = svd.getS().getMatrix(0, k, 0, k);
        V = svd.getV().getMatrix(0, svd.getV().getRowDimension() - 1, 0, k);
        M = Sigma.times(V.transpose());

        logger.info("======== Finished computing SVD ========");
    }

    protected int computeNumberOfSingularValuesToKeep(
            SingularValueDecomposition svd
    ) {
        logger.info("======== Computing rank of SVD matrices for prediction ========");
        Matrix sigma = svd.getS();
        double totalEnergy = 0;
        double retainedEnergy = 0;

        for(int row = 0; row < sigma.getRowDimension(); row++) {
            totalEnergy += Math.pow(sigma.get(row, row), 2);
        }

        logger.info("Full rank matrix has a total energy of {}", totalEnergy);

        for(int row = 0; row < sigma.getRowDimension(); row++) {
            retainedEnergy += Math.pow(sigma.get(row, row), 2);
            double energyRatio = retainedEnergy / totalEnergy;
            if(energyRatio >= 0.8) {
                logger.info("========= Matrix of rank {} retains {}% of energy =========",
                        row, energyRatio * 100.0);
                return row;
            }
        }

        return sigma.getColumnDimension() - 1;
    }

    @Override
    public double predictScore(int user, int movie) {
        Matrix userVector = U.getMatrix(user, user, 0, U.getColumnDimension() - 1);
        Matrix movieVector = M.getMatrix(0, M.getRowDimension() - 1, movie, movie);
        Matrix prediction = userVector.times(movieVector);
        return prediction.get(0, 0);
    }
}

package hex.genmodel.algos.tree;

import hex.genmodel.utils.ArrayUtils;

import java.util.Arrays;

public class ContributionComposer {
    
    /**
     * Sort #contribNameIds according to #contribs values and compose desired output with correct #topN, #topBottomN fields
     *
     * @param contribNameIds Contribution corresponding feature ids
     * @param contribs Raw contribution values
     * @param topN Return only #topN highest #contribNameIds + bias.
     * @param topBottomN Return only #topBottomN lowest #contribNameIds + bias
     *                   If #topN and #topBottomN are defined together then return array of #topN + #topBottomN + bias
     * @param abs True to compare absolute values of #contribs
     * @return Sorted contribNameIds array of corresponding contributions features.
     *         The size of returned array is #topN + #topBottomN + bias
     */
    public final int[] composeContributions(final int[] contribNameIds, final float[] contribs, int topN, int topBottomN, boolean abs) {
        assert contribNameIds.length == contribs.length : "contribNameIds must have the same length as contribs";
        if (returnOnlyTopN(topN, topBottomN)) {
            return composeSortedContributions(contribNameIds, contribs, topN, abs, -1);
        } else if (returnOnlyTopBottomN(topN, topBottomN)) {
            return composeSortedContributions(contribNameIds, contribs, topBottomN, abs,1);
        } else if (returnAllTopN(topN, topBottomN, contribs.length)) {
            return composeSortedContributions(contribNameIds, contribs, contribs.length, abs, -1);
        }

        composeSortedContributions(contribNameIds, contribs, contribNameIds.length, abs,-1);
        int[] bottomSorted = Arrays.copyOfRange(contribNameIds, contribNameIds.length - 1 - topBottomN, contribNameIds.length);
        reverse(bottomSorted, contribs, bottomSorted.length - 1);
        int[] contribNameIdsTmp = Arrays.copyOf(contribNameIds, topN);

        return ArrayUtils.append(contribNameIdsTmp, bottomSorted);
    }

    private boolean returnOnlyTopN(int topN, int topBottomN) {
        return topN != 0 && topBottomN == 0;
    }

    private boolean returnOnlyTopBottomN(int topN, int topBottomN) {
        return topN == 0 && topBottomN != 0;
    }

    private boolean returnAllTopN(int topN, int topBottomN, int len) {
        return (topN + topBottomN) >= len || topN < 0 || topBottomN < 0;
    }

    public int checkAndAdjustInput(int n, int len) {
        if (n < 0 || n > len) {
            return len;
        }
        return n;
    }
    
    private int[] composeSortedContributions(final int[] contribNameIds, final float[] contribs, int n, boolean abs, int increasing) {
        int nAdjusted = checkAndAdjustInput(n, contribs.length);
        sortContributions(contribNameIds, contribs, abs, increasing);
        if (nAdjusted < contribs.length) {
            int bias = contribNameIds[contribs.length-1];
            int[] contribNameIdsSorted = Arrays.copyOfRange(contribNameIds, 0, nAdjusted + 1);
            contribNameIdsSorted[nAdjusted] = bias;
            return contribNameIdsSorted;
        }
        return contribNameIds;
    }
    
    private void sortContributions(final int[] contribNameIds, final float[] contribs, final boolean abs, final int increasing) {
        ArrayUtils.sort(contribNameIds, contribs, 0, contribs.length -1, abs, increasing);
    }

    private void reverse(int[] contribNameIds, float[] contribs, int len) {
        for (int i = 0; i < len/2; i++) {
            if (contribs[contribNameIds[i]] != contribs[contribNameIds[len - i - 1]]) {
                int tmp = contribNameIds[i];
                contribNameIds[i] = contribNameIds[len - i - 1];
                contribNameIds[len - i - 1] = tmp;
            }
        }
    }
}

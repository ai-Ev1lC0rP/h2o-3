import sys

sys.path.insert(1, "../../../")
from tests import pyunit_utils
from tests.pyunit_utils import dataset_prostate
from h2o.estimators.xgboost import H2OXGBoostEstimator


def assert_same_scoring_history(model_actual, model_expected, metric_name1, metric_name2, msg=None):
    scoring_history_actual = model_actual.scoring_history()
    scoring_history_expected = model_expected.scoring_history()
    sh1 = scoring_history_actual[metric_name1]
    sh2 = scoring_history_expected[metric_name2]
    assert (sh1 - sh2).abs().max() < 1e-4, msg


def test_eval_metric_early_stopping():
    (train, _, _) = dataset_prostate()
    model_expected = H2OXGBoostEstimator(model_id="prostate_mae", ntrees=1000, max_depth=5,
                                         score_each_iteration=True,
                                         stopping_metric="mae",
                                         stopping_tolerance=0.1,
                                         stopping_rounds=3,
                                         seed=123)
    model_expected.train(y="AGE", x=train.names, training_frame=train)

    model_actual = H2OXGBoostEstimator(model_id="prostate_custom", ntrees=1000, max_depth=5,
                                       score_each_iteration=True,
                                       eval_metric="mae",
                                       stopping_metric="custom",
                                       stopping_tolerance=0.1,
                                       stopping_rounds=3,
                                       seed=123)
    model_actual.train(y="AGE", x=train.names, training_frame=train)

    assert_same_scoring_history(model_actual, model_expected, "training_custom", "training_mae")


if __name__ == "__main__":
    pyunit_utils.standalone_test(test_eval_metric_early_stopping)
else:
    test_eval_metric_early_stopping()

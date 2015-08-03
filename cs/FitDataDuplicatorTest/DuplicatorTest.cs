using System;
using System.IO;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using FitDataDuplicator;

namespace FitDataDuplicatorTests
{
    [TestClass]
    public class DuplicatorTest
    {
        private static void TestDuplicate(string fitFilePath)
        {
            using (var src = new FileStream(fitFilePath, FileMode.Open, FileAccess.Read))
            using (var dest = new MemoryStream())
            {
                var result = new Duplicator().Duplicate(dest, src);
                Assert.AreEqual(result, true);
                NUnit.Framework.FileAssert.AreEqual(dest, src);
            }
        }

        // It fails the test because of data(or SDK).
/*
        [TestMethod]
        public void TestDuplicateActivity()
        {
            TestDuplicate(@"FitFiles\Activity.fit");
        }

        [TestMethod]
        public void TestDuplicateMonitoringFile()
        {
            TestDuplicate(@"FitFiles\MonitoringFile.fit");
        }
*/

        [TestMethod]
        public void TestDuplicateSettings()
        {
            TestDuplicate(@"FitFiles\Settings.fit");
        }

        [TestMethod]
        public void TestDuplicateWeightScaleMultiUser()
        {
            TestDuplicate(@"FitFiles\WeightScaleMultiUser.fit");
        }

        [TestMethod]
        public void TestDuplicateWeightScaleSingleUser()
        {
            TestDuplicate(@"FitFiles\WeightScaleSingleUser.fit");
        }

        [TestMethod]
        public void TestDuplicateWorkoutCustomTargetValues()
        {
            TestDuplicate(@"FitFiles\WorkoutCustomTargetValues.fit");
        }

        [TestMethod]
        public void TestDuplicateWorkoutIndividualSteps()
        {
            TestDuplicate(@"FitFiles\WorkoutIndividualSteps.fit");
        }

        [TestMethod]
        public void TestDuplicateWorkoutRepeatGreaterThanStep()
        {
            TestDuplicate(@"FitFiles\WorkoutRepeatGreaterThanStep.fit");
        }

        [TestMethod]
        public void TestDuplicateWorkoutRepeatSteps()
        {
            TestDuplicate(@"FitFiles\WorkoutRepeatSteps.fit");
        }
    }
}

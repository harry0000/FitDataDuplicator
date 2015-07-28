using FitDataDuplicator;

using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace FitDataDuplicatorTests
{
    [TestClass]
    public class ProgramTest
    {
        [TestMethod]
        public void TestGetDestFilePath()
        {
            const string expected = @"C:\Garmin\Activities\1990-01-01-00-00-00_duplicated.fit";
            const string srcPath = @"C:\Garmin\Activities\1990-01-01-00-00-00.fit";

            var pt = new PrivateType(typeof(Program));
            var actual = (string)pt.InvokeStatic("GetDestFilePath", new object[] { srcPath });

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void TestGetDestFilePath_NoExtensions()
        {
            const string expected = @"C:\Garmin\Activities\1990-01-01-00-00-00_duplicated";
            const string srcPath = @"C:\Garmin\Activities\1990-01-01-00-00-00";

            var pt = new PrivateType(typeof(Program));
            var actual = (string)pt.InvokeStatic("GetDestFilePath", new object[] { srcPath });

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void TestGetDestFilePath_MultipleExtensions()
        {
            const string expected = @"C:\Garmin\Activities\1990-01-01-00-00-00_duplicated.fit.bkup";
            const string srcPath = @"C:\Garmin\Activities\1990-01-01-00-00-00.fit.bkup";

            var pt = new PrivateType(typeof(Program));
            var actual = (string)pt.InvokeStatic("GetDestFilePath", new object[] { srcPath });

            Assert.AreEqual(expected, actual);
        }
    }
}

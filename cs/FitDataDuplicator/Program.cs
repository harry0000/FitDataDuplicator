using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FitDataDuplicator
{
    public class Program
    {
        const string DestFileSuffix = "_duplicated";

        static string GetDestFilePath(string srcPath)
        {
            var name = Path.GetFileName(srcPath);
            var idx = name.IndexOf('.');
            if (idx >= 0)
            {
                return Path.GetDirectoryName(srcPath) + '\\' + name.Insert(idx, DestFileSuffix);
            }

            return srcPath + DestFileSuffix;
        }

        static void Main(string[] args)
        {
            foreach (string arg in args)
            {
                if (!File.Exists(arg))
                {
                    Console.WriteLine("\"{0}\" does not exist.", arg);
                    continue;
                }

                using (var src = new FileStream(arg, FileMode.Open, FileAccess.Read))
                {
                    if (!new Dynastream.Fit.Decode().IsFIT(src))
                    {
                        Console.WriteLine("\"{0}\" is not FIT File.", arg);
                        continue;
                    }

                    using (var dest = new FileStream(GetDestFilePath(src.Name), FileMode.Create, FileAccess.ReadWrite))
                    {
                        var result = Duplicator.Duplicate(dest, src);
                        Console.WriteLine("\"{0}\" is duplicated. result: {1}", src.Name, result);
                    }
                }
            }
        }
    }
}

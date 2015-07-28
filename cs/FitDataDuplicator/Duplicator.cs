using Dynastream.Fit;

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FitDataDuplicator
{
    public class Duplicator
    {
        readonly static MesgDefinition[] mesgDefinitions = new MesgDefinition[Fit.MaxLocalMesgs];
        static Stream fitDest;

        private Duplicator() { }

        public static bool Duplicate(Stream dest, Stream src)
        {
            var decode = new Decode();
            if (!decode.CheckIntegrity(src))
            {
                Console.WriteLine("FIT file integrity failed.");
                return false;
            }

            fitDest = dest;

            // Copy header.
            var header = new Header(src);
            header.Write(fitDest);

            // Copy body.
            decode.MesgDefinitionEvent += OnMesgDefinition;
            decode.MesgEvent += OnMesg;
            var result = decode.Read(src);

            // Update header. (data size)
            fitDest.Position = 4;
            fitDest.Write(BitConverter.GetBytes((uint)(fitDest.Length - header.Size)), 0, 4);

            // Update CRC.
            var data = new byte[fitDest.Length];
            fitDest.Position = 0;
            fitDest.Read(data, 0, data.Length);
            fitDest.Write(BitConverter.GetBytes(CRC.Calc16(data, data.Length)), 0, 2);

            fitDest = null;
            Array.Clear(mesgDefinitions, 0, mesgDefinitions.Length);

            return result;
        }

        static void OnMesgDefinition(object sender, MesgDefinitionEventArgs e)
        {
            if (fitDest == null)
            {
                return;
            }

            // Store MesgDefinition.
            mesgDefinitions[e.mesgDef.LocalMesgNum] = e.mesgDef;

            e.mesgDef.Write(fitDest);
        }

        static void OnMesg(object sender, MesgEventArgs e)
        {
            if (fitDest == null)
            {
                return;
            }

            var mesgDef = mesgDefinitions[e.mesg.LocalNum];

            // Remove expansion fields.
            var rawCount = mesgDef.GetFields().Count;
            var count = e.mesg.fields.Count;
            if (count > rawCount)
            {
                e.mesg.fields.RemoveRange(rawCount, count - rawCount);
            }

            e.mesg.Write(fitDest, mesgDef);
        }
    }
}

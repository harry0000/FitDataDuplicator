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
        readonly MesgDefinition[] mesgDefinitions = new MesgDefinition[Fit.MaxLocalMesgs];
        Stream fitDest;

        public Duplicator() { }

        public bool Duplicate(Stream dest, Stream src)
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
            decode.MesgDefinitionEvent += this.OnMesgDefinition;
            decode.MesgEvent += this.OnMesg;
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

        void OnMesgDefinition(object sender, MesgDefinitionEventArgs e)
        {
            if (fitDest == null)
            {
                return;
            }

            // Store MesgDefinition.
            var mesgDef = e.mesgDef;
            mesgDefinitions[mesgDef.LocalMesgNum] = mesgDef;

            if (mesgDef.IsBigEndian)
            {
                // Fit.BigEndian
                BinaryWriter bw = new BinaryWriter(fitDest);  
                bw.Write((byte)(mesgDef.LocalMesgNum + Fit.MesgDefinitionMask));
                bw.Write((byte)Fit.MesgDefinitionReserved);
                bw.Write((byte)Fit.BigEndian);
                bw.Write((byte)(mesgDef.GlobalMesgNum >> 8));
                bw.Write((byte)mesgDef.GlobalMesgNum);
                bw.Write(mesgDef.NumFields);

                if (mesgDef.NumFields != mesgDef.GetFields().Count)
                {
                    throw new FitException("MesgDefinition:Write - Field Count Internal Error");
                }
                foreach (var fieldDef in mesgDef.GetFields())
                {
                    bw.Write(fieldDef.Num);
                    bw.Write(fieldDef.Size);
                    bw.Write(fieldDef.Type);
                }
            }
            else
            {
                // Fit.LittleEndian
                mesgDef.Write(fitDest);
            }
        }

        void OnMesg(object sender, MesgEventArgs e)
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

using System;
using System.Runtime;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using Plang.CSharpRuntime;
using Plang.CSharpRuntime.Values;
using Plang.CSharpRuntime.Exceptions;
using System.Threading;
using System.Threading.Tasks;

#pragma warning disable 162, 219, 414
namespace PImplementation
{
    public static partial class GlobalFunctions {
		public static PrtNamedTuple ChooseRandomTransaction(PMachine pMachine)
        {
            return (new PrtNamedTuple(new string[] { "key", "val", "transId" }, (PrtString) pMachine.TryRandomInt(100).ToString(), (PrtInt)pMachine.TryRandomInt(100), (PrtInt)pMachine.TryRandomInt(100)));
        }
	}
}
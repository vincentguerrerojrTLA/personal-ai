package com.kylow.mobile.release

data class GateResult(val allowed:Boolean,val failures:List<String>)
object ReleaseGate{
 fun evaluate(tests:Boolean,privacy:Boolean,security:Boolean,safety:Boolean,dependencies:Boolean):GateResult{
  val f=buildList{if(!tests)add("tests");if(!privacy)add("privacy");if(!security)add("security");if(!safety)add("safety");if(!dependencies)add("dependencies")}
  return GateResult(f.isEmpty(),f)
 }
}

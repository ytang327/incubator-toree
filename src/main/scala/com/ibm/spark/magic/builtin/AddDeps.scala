package com.ibm.spark.magic.builtin

import com.ibm.spark.magic.MagicOutput
import com.ibm.spark.magic.dependencies.{IncludeDependencyDownloader, IncludeSparkContext, IncludeOutputStream, IncludeInterpreter}
import com.ibm.spark.dependencies.IvyDependencyDownloader
import java.io.PrintStream
import com.ibm.spark.SparkKernelOptions
import com.ibm.spark.utils.ArgumentParsingSupport

class AddDeps extends MagicTemplate with IncludeInterpreter
  with IncludeOutputStream with IncludeSparkContext with ArgumentParsingSupport
  with IncludeDependencyDownloader
{

  private lazy val printStream = new PrintStream(outputStream)

  val _transitive =
    parser.accepts("transitive", "retrieve dependencies recursively")
    .withOptionalArg().ofType(classOf[Boolean]).defaultsTo(true)

  /**
   * Execute a magic representing a cell magic.
   * @param code The list of code, separated by newlines
   * @return The output of the magic
   */
  override def executeCell(code: Seq[String]): MagicOutput = {
    code.foreach(executeLine)
    MagicOutput()
  }

  /**
   * Execute a magic representing a line magic.
   * @param code The single line of code
   * @return The output of the magic
   */
  override def executeLine(code: String): MagicOutput = {
    val nonOptionArgs = parseArgs(code)
    dependencyDownloader.setPrintStream(printStream)

    // TODO: require a version or use the most recent if omitted?
    if (nonOptionArgs.size != 3) {
      printHelp(printStream, """%AddDeps my.company artifact-id version""")
      return MagicOutput()
    }

    // get the jars and hold onto the paths at which they reside
    val urls = dependencyDownloader.retrieve(
      nonOptionArgs(0), nonOptionArgs(1), nonOptionArgs(2), _transitive)

    // add the jars to the interpreter and spark context
    interpreter.addJars(urls:_*)
    urls.foreach(url => sparkContext.addJar(url.getPath))

    // TODO: report issues, etc, to the user or is the ivy output enough?
    MagicOutput()
  }
}

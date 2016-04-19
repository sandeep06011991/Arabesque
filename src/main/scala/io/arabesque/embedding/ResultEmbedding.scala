package io.arabesque.embedding

import org.apache.hadoop.io.Writable
import java.io.DataOutput
import java.io.DataInput

trait ResultEmbedding extends Writable {
  def words: Array[Int]
  
  override def write(out: DataOutput): Unit = {
    out.writeInt (words.size)
    words.foreach (w => out.writeInt(w))
  }

}

object ResultEmbedding {
  def apply (strEmbedding: String) = {
    if (strEmbedding contains "-")
      EEmbedding (strEmbedding)
    else
      VEmbedding (strEmbedding)
  }
  def apply(embedding: Embedding) = {
    if (embedding.isInstanceOf[EdgeInducedEmbedding])
      EEmbedding (embedding.getEdges.toIntArray)
    else
      VEmbedding (embedding.getVertices.toIntArray)
  }
}
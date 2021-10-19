package io.github.nicholas_roether.algorithm;

import io.github.nicholas_roether.JSONSerializable;
import io.github.nicholas_roether.general.NodePosition;

/**
 * The data of nodes of graphs that the A*-algorithm is executed on needs to extend this class.
 * <br>
 * This guarantees that the algorithm can find the position of the node on the screen, and that the data is
 * JSON-serializable.
 */
public abstract class AStarNodeData implements NodePosition, JSONSerializable {}

package com.team48.applikasjon.data.models

/* Dataklasse basert på JSON-formatet på MET-API */
data class VectorDataset(
    val imageType: String?,
    val url: String?,
    val name: String?
    )

/* Justert MET-versjon av VectorTile-dataklassen */
data class VectorTile(
    val bounds: List<Number>?,
    val center: List<Number>?,
    val description: String?,
    val format: String?,
    val generator: String?,
    val generator_options: String?,
    val map: String?,
    val maxzoom: Number?,
    val minzoom: Number?,
    val name: String?,
    val scheme: String?,
    val tilejson: String?,
    val tiles: List<String>?,
    val tilestats: Tilestats?,
    val type: String?,
    val vector_layers: List<Vector_layers>?,
    val version: String?
) {
    fun getTileId() = tilestats?.layers?.get(0)?.layer.toString()
}

/*  Resterende dataklasser er basert på innhold i metadata på API */
data class Attributes(
    val attribute: String?,
    val count: Number?,
    val max: Number?,
    val min: Number?,
    val type: String?,
    val values: List<Number>?
)

data class Fields(
    val max: String?,
    val min: String?,
    val time: String?,
    val value: String?
)

data class Layers(
    val attributeCount: Number?,
    val attributes: List<Any>?,
    val count: Number?,
    val geometry: String?,
    val layer: String?
)

data class Tilestats(
    val layerCount: Number?,
    val layers: List<Layers>?
)

data class Vector_layers(
    val description: String?,
    val fields: Fields?,
    val id: String?,
    val maxzoom: Number?,
    val minzoom: Number?
)


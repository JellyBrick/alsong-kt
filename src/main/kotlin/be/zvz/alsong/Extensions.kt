package be.zvz.alsong

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.ResponseHandler
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.result.Result
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.InputStream
import java.io.Reader

inline fun <reified T : Any> Request.responseObject(
    loader: DeserializationStrategy<T>,
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
    noinline deserializer: (Request, Response, Result<T, FuelError>) -> Unit,
) = response(kotlinxDeserializerOf(loader, xml), deserializer)

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Request.responseObject(
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
    noinline deserializer: (Request, Response, Result<T, FuelError>) -> Unit,
) = responseObject(T::class.serializer(), xml, deserializer)

inline fun <reified T : Any> Request.responseObject(
    deserializer: ResponseHandler<T>,
    loader: DeserializationStrategy<T>,
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = response(kotlinxDeserializerOf(loader, xml), deserializer)

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Request.responseObject(
    deserializer: ResponseHandler<T>,
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = responseObject(deserializer, T::class.serializer(), xml)

inline fun <reified T : Any> Request.responseObject(
    loader: DeserializationStrategy<T>,
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = response(kotlinxDeserializerOf(loader, xml))

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Request.responseObject(
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = responseObject(T::class.serializer(), xml)

inline fun <reified T : Any> kotlinxDeserializerOf(
    loader: DeserializationStrategy<T>,
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = object : ResponseDeserializable<T> {
    override fun deserialize(content: String): T = xml.decodeFromString(loader, content)
    override fun deserialize(reader: Reader): T = deserialize(reader.readText())
    override fun deserialize(bytes: ByteArray): T = deserialize(String(bytes))

    override fun deserialize(inputStream: InputStream): T {
        inputStream.bufferedReader().use {
            return deserialize(it)
        }
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> kotlinxDeserializerOf(
    xml: XML = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Charset
        autoPolymorphic = true
    },
) = kotlinxDeserializerOf(T::class.serializer(), xml)

package net.q3aiml.streampath;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author q3aiml
 */
public class DocumentSets {

    private static final ImmutableDocumentSet EMPTY_DOCUMENT_SET
            = new ImmutableDocumentSet(ImmutableSet.<Document>of());

    public static DocumentSet empty() {
        return EMPTY_DOCUMENT_SET;
    }

    public static DocumentSetBuilder create() {
        return new DocumentSetBuilder();
    }

    public static DocumentSet ofSource(Source currentDocument) {
        return create().add(null, currentDocument).build();
    }

    private static class ImmutableDocumentSet implements DocumentSet {
        private final ImmutableSet<Document> documents;

        public ImmutableDocumentSet(ImmutableSet<Document> documents) {
            this.documents = documents;
        }

        public Set<Document> documents() {
            return documents;
        }

        @Override
        public void close() throws IOException { }
    }

    private static class StreamDocument implements Document {
        private final String identifier;
        private final Source input;

        public StreamDocument(String identifier, Source input) {
            this.identifier = identifier;
            this.input = checkNotNull(input, "input Source must not be null");
        }

        @Nullable
        @Override
        public String identifier() {
            return identifier;
        }

        public XMLEventReader stream() throws IOException {
            try {
                return XMLInputFactory.newInstance().createXMLEventReader(input);
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }

        public void close() throws IOException {
            // don't need to close Source
        }
    }

    public static class DocumentSetBuilder {
        private final ImmutableSet.Builder<Document> documents = ImmutableSet.builder();

        private DocumentSetBuilder() { }

        public DocumentSetBuilder add(String identifier, Source source) {
            documents.add(new StreamDocument(identifier, source));
            return this;
        }

        public DocumentSet build() {
            return new ImmutableDocumentSet(documents.build());
        }
    }
}

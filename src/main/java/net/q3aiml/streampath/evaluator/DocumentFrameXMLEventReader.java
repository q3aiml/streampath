package net.q3aiml.streampath.evaluator;

import com.google.common.collect.AbstractIterator;
import net.q3aiml.streampath.Document;
import net.q3aiml.streampath.InvalidDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author q3aiml
 */
public class DocumentFrameXMLEventReader {
    private static final Logger log = LoggerFactory.getLogger(DocumentFrameXMLEventReader.class);

    public Iterable<Frame> read(XMLEventReader reader) throws XMLStreamException {
        return new DocumentFrameReaderIterable(reader, null);
    }

    public Iterable<Frame> read(XMLEventReader reader, Document document) throws XMLStreamException {
        return new DocumentFrameReaderIterable(reader, document);
    }

    private static class DocumentFrameReaderIterable implements Iterable<Frame> {
        private final XMLEventReader reader;
        private final Document document;

        private DocumentFrameReaderIterable(XMLEventReader reader, Document document) {
            this.reader = reader;
            this.document = document;
        }

        @Override
        public Iterator<Frame> iterator() {
            return new DocumentFrameReaderIterator(reader, document);
        }
    }

    private static class DocumentFrameReaderIterator extends AbstractIterator<Frame> {
        private final XMLEventReader reader;
        private final Document document;

        StackFrame stack;
        StringBuffer content = null;

        private DocumentFrameReaderIterator(XMLEventReader reader, Document document) {
            this.reader = reader;
            this.document = document;
            stack = new StackFrame(document);
        }

        @Override
        protected Frame computeNext() {
            while (reader.hasNext()) {
                XMLEvent event;
                try {
                    event = reader.nextEvent();
                } catch (XMLStreamException e) {
                    throw new RuntimeException(new InvalidDocumentException(
                            "document " + document + " was not XML as expected", document, e));
                }

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    stack = new StackFrame(document, stack, startElement);
                    content = new StringBuffer();
                } else if (event.isEndElement()) {
                    StackFrame currentStack = stack;
                    if (content != null) {
                        currentStack.contents(content.toString());
                    }
                    stack = stack.parent();
                    content = null;

                    return currentStack;
                } else if (event.isCharacters()) {
                    if (content != null) {
                        content.append(event.asCharacters().getData());
                    }
                } else if (event.isStartDocument() || event.isEndDocument()) {
                    log.trace("ignoring xml event {}", event);
                } else {
                    log.warn("unhandled xml event " + event);
                }
            }

            checkState(stack.isRoot(), "reached end of input but stack is not at root: " + stack);
            checkState(content == null, "reached end of input but character buffer remains: " + content);
            return endOfData();
        }
    }

    private static class StackFrame implements Frame {
        private final Document document;
        private final StackFrame parent;
        private final StartElement startElement;
        private String contents;

        public StackFrame(Document document) {
            this.document = document;
            this.parent = null;
            this.startElement = null;
        }

        public StackFrame(Document document, StackFrame parent, StartElement startElement) {
            this.document = document;
            this.parent = parent;
            this.startElement = checkNotNull(startElement, "startElement must not be null");
        }

        @Override
        public Document document() {
            return document;
        }

        @Override
        public StackFrame parent() {
            return parent;
        }

        @Override
        public String name() {
            checkState(!isRoot(), "tried to get name() on root Frame");
            return startElement.getName().getLocalPart();
        }

        @Override
        public boolean isRoot() {
            return startElement == null;
        }

        @Override
        public String contents() {
            return contents;
        }

        private void contents(String contents) {
            this.contents = contents;
        }

        @Override
        public AttributeCollection attributes() {
            return new AttributeCollectionAdaptor(startElement);
        }

        @Override
        public String toString() {
            return "StackFrame{ " + startElement + " } -> " + parent;
        }
    }

    private static class AttributeCollectionAdaptor implements AttributeCollection {
        private final StartElement startElement;

        public AttributeCollectionAdaptor(StartElement startElement) {
            this.startElement = startElement;
        }

        @Override
        public Iterator<Attribute> iterator() {
            //noinspection unchecked
            return (Iterator<Attribute>)startElement.getAttributes();
        }

        @Override
        @Nullable
        public Attribute attribute(QName name) {
            for (Attribute attribute : this) {
                if (name.equals(attribute.getName())) {
                    return attribute;
                }
            }
            return null;
        }
    }
}

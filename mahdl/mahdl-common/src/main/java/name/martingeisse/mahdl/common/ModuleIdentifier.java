package name.martingeisse.mahdl.common;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class ModuleIdentifier implements Iterable<String> {

    private static final Pattern SEGMENT_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    public static boolean isValidSegment(String s) {
        return SEGMENT_PATTERN.matcher(s).matches();
    }

    private final String[] segments;

    public ModuleIdentifier(String[] segments) {
        if (segments == null) {
            throw new IllegalArgumentException("segments is null");
        }
        if (segments.length == 0) {
            throw new IllegalArgumentException("segments is empty");
        }
        for (String segment : segments) {
            if (segment == null) {
                throw new IllegalArgumentException("segments contains null entry");
            }
            if (!isValidSegment(segment)) {
                throw new IllegalArgumentException("segments contains invalid segment: " + segment);
            }
        }
        this.segments = segments.clone();
    }

    public ModuleIdentifier(List<String> segments) {
        if (segments == null) {
            throw new IllegalArgumentException("segments is null");
        }
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("segments is empty");
        }
        for (String segment : segments) {
            if (segment == null) {
                throw new IllegalArgumentException("segments contains null entry");
            }
            if (!isValidSegment(segment)) {
                throw new IllegalArgumentException("segments contains invalid segment: " + segment);
            }
        }
        this.segments = segments.toArray(new String[0]);
    }

    public ModuleIdentifier(QualifiedModuleName cmName) {
        if (cmName == null) {
            throw new IllegalArgumentException("cmName is null");
        }
        if (cmName.getSegments() == null) {
            throw new IllegalArgumentException("cmName contains null token list");
        }
        List<CmToken> tokens = cmName.getSegments().getAll();
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("cmName contains empty token list");
        }
        segments = new String[tokens.size()];
        for (int i = 0; i < segments.length; i++) {
            String text = tokens.get(i).getText();
            if (text == null) {
                throw new IllegalArgumentException("cmName contains token with null text");
            }
            segments[i] = text;
        }
    }

    public String[] getSegments() {
        return segments.clone();
    }

    public int getSegmentCount() {
        return segments.length;
    }

    public String getSegment(int index) {
        return segments[index];
    }

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModuleIdentifier)) {
            return false;
        }
        ModuleIdentifier other = (ModuleIdentifier) o;
        return Arrays.equals(segments, other.segments);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(segments);
    }

    @Override
    public String toString() {
        return toString('.');
    }

    public String toString(char separator) {
        return StringUtils.join(segments, separator);
    }

    public String packageToString() {
        return packageToString('.');
    }

    public String packageToString(char separator) {
        return StringUtils.join(segments, separator, 0, segments.length - 1);
    }

    public String localNameToString() {
        return segments[segments.length - 1];
    }

}

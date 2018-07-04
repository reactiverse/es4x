import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;

@TargetClass(className = "io.netty.util.internal.logging.InternalLoggerFactory")
final class TargetInternalLoggerFactory {
  @Substitute
  private static InternalLoggerFactory newDefaultFactory(String name) {
    return JdkLoggerFactory.INSTANCE;
  }
}

@TargetClass(className = "io.netty.util.internal.CleanerJava6")
final class TargetCleanerJava6 {
  @Alias
  @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FieldOffset, declClassName = "java.nio.DirectByteBuffer", name = "cleaner")
  private static long CLEANER_FIELD_OFFSET;
}

@TargetClass(className = "io.netty.util.internal.PlatformDependent0")
final class TargetPlatformDependent0 {
  @Alias
  @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FieldOffset, declClassName = "java.nio.Buffer", name = "address")
  private static long ADDRESS_FIELD_OFFSET;
}

@TargetClass(io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess.class)
final class TargetUnsafeRefArrayAccess {
  @Alias
  @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.ArrayIndexShift, declClass = Object[].class)
  public static int REF_ELEMENT_SHIFT;
}

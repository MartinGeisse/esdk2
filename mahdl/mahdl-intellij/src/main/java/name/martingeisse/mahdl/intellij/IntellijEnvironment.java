package name.martingeisse.mahdl.intellij;

import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.CmUtil;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import name.martingeisse.mahdl.input.cm.impl.InternalPsiUtil;
import name.martingeisse.mahdl.input.cm.impl.QualifiedModuleNameImpl;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class IntellijEnvironment implements Environment {

	public static void initialize() {
		if (Holder.INSTANCE == null) {
			Holder.INSTANCE = new IntellijEnvironment();
		}
	}

	@Override
	public InputStream openDataFile(CmNode anchor, String filename) throws IOException {
		PsiElement anchorPsi = InternalPsiUtil.getPsiFromCm(anchor);

		if (filename.indexOf('/') != -1 || filename.startsWith(".")) {
			throw new IOException("invalid filename: " + filename);
		}
		PsiFile psiFile = anchorPsi.getContainingFile();
		if (psiFile == null) {
			throw new IOException("element is not inside a PsiFile");
		}
		VirtualFile containingFile = psiFile.getOriginalFile().getVirtualFile();
		if (containingFile == null) {
			throw new IOException("element is not inside a VirtualFile");
		}
		VirtualFile folder = containingFile.getParent();
		VirtualFile file = folder.findChild(filename);
		if (file == null) {
			throw new IOException("file not found: " + filename);
		}
		return file.getInputStream();
	}

	@Override
	public void validateModuleNameAgainstFilePath(Module mahdlModule, QualifiedModuleName name) throws IOException {

		// determine the IntelliJ project module
		QualifiedModuleNameImpl namePsi = (QualifiedModuleNameImpl) InternalPsiUtil.getPsiFromCm(name);
		PsiFile psiFile = namePsi.getContainingFile();
		if (psiFile == null) {
			return;
		}
		VirtualFile virtualFile = psiFile.getOriginalFile().getVirtualFile();
		if (virtualFile == null) {
			return;
		}
		com.intellij.openapi.module.Module projectModule =
			ProjectRootManager.getInstance(psiFile.getProject()).getFileIndex().getModuleForFile(virtualFile);
		if (projectModule == null) {
			return;
		}

		// determine the path of the source file relative to any one of the module roots
		VirtualFile[] moduleRoots = ModuleRootManager.getInstance(projectModule).getContentRoots();
		StringBuilder pathBuilder = new StringBuilder(virtualFile.getName());
		VirtualFile currentFolder = virtualFile.getParent();
		folderLoop: while (true) {
			if (currentFolder == null) {
				return;
			}
			for (VirtualFile moduleRoot : moduleRoots) {
				if (currentFolder.equals(moduleRoot)) {
					break folderLoop;
				}
			}
			pathBuilder.insert(0, currentFolder.getName() + '/');
			currentFolder = currentFolder.getParent();
		}
		String path = pathBuilder.toString();

		// analyze the path just determined
		String expectedPrefix = "src/mahdl/";
		String expectedSuffix = ".mahdl";
		if (!path.startsWith(expectedPrefix)) {
			throw new IOException("file should be located within src/mahdl");
		}
		if (!path.endsWith(expectedSuffix)) {
			throw new IOException("file should have extension .mahdl");
		}
		String qualifiedNameFromPath = path.substring(expectedPrefix.length(), path.length() - expectedSuffix.length()).replace('/', '.');
		String qualifiedNameFromCm = CmUtil.canonicalizeQualifiedModuleName(name);
		if (!qualifiedNameFromCm.equals(qualifiedNameFromPath)) {
			throw new IOException("file should contain module " + qualifiedNameFromPath + ", contains " + qualifiedNameFromCm);
		}

	}

	@Override
	public ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException {
		QualifiedModuleNameImpl namePsi = (QualifiedModuleNameImpl) InternalPsiUtil.getPsiFromCm(name);
		PsiElement untypedResolvedModule = namePsi.getReference().resolve();
		if (untypedResolvedModule instanceof Module) {
			return new ModuleApi((Module) untypedResolvedModule);
		} else {
			throw new ReferenceResolutionException("unknown module: '" + namePsi.getReference().getCanonicalText() + "'");
		}
	}

}

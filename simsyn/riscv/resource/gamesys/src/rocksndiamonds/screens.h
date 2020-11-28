// ============================================================================
// Rocks'n'Diamonds - McDuffin Strikes Back!
// ----------------------------------------------------------------------------
// (c) 1995-2014 by Artsoft Entertainment
//     		    Holger Schemel
//		    info@artsoft.org
//		    https://www.artsoft.org/
// ----------------------------------------------------------------------------
// screens.h
// ============================================================================

#ifndef SCREENS_H
#define SCREENS_H

#include "main.h"

// (arbitrary, but unique) values for HandleChooseTree()
#define SCROLL_LINE	(1 * SCR_FIELDY)
#define SCROLL_PAGE	(2 * SCR_FIELDY)


void DrawMainMenuExt(int);
void DrawAndFadeInMainMenu(int);
void DrawMainMenu(void);
void DrawHallOfFame(int, int);
void DrawScreenAfterAddingSet(char *, int);

void RedrawSetupScreenAfterFullscreenToggle(void);
void RedrawSetupScreenAfterScreenRotation(int);

void HandleTitleScreen(int, int, int, int, int);
void HandleMainMenu(int, int, int, int, int);
void HandleChooseLevelSet(int, int, int, int, int);
void HandleChooseLevelNr(int, int, int, int, int);
void HandleHallOfFame(int, int, int, int, int);
void HandleInfoScreen(int, int, int, int, int);
void HandleSetupScreen(int, int, int, int, int);
void HandleTypeName(int, Key);
void HandleGameActions(void);

void CreateScreenGadgets(void);
void FreeScreenGadgets(void);

void setHideRelatedSetupEntries(void);

void DumpScreenIdentifiers(void);
boolean DoScreenAction(int);

#endif	// SCREENS_H

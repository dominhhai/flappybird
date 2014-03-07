//
//  AppDelegate.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/6/14.
//  Copyright Hai Do Minh 2014. All rights reserved.
//
// -----------------------------------------------------------------------
#import "AppDelegate.h"
#import "MenuScene.h"
#import "HelloWorldScene.h"
#import "GameConfig.h"

@implementation AppDelegate

// 
-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
	// This is the only app delegate method you need to implement when inheriting from CCAppDelegate.
	// This method is a good place to add one time setup code that only runs when your app is first launched.
	
	// Setup Cocos2D with reasonable defaults for everything.
	// There are a number of simple options you can change.
	// If you want more flexibility, you can configure Cocos2D yourself instead of calling setupCocos2dWithOptions:.
	[self setupCocos2dWithOptions:@{
		// Show the FPS and draw call label.
		CCSetupShowDebugStats: @(YES),
		
		// More examples of options you might want to fiddle with:
		// (See CCAppDelegate.h for more information)
		
		// Use a 16 bit color buffer: 
//		CCSetupPixelFormat: kEAGLColorFormatRGB565,
		// Use a simplified coordinate system that is shared across devices.
		CCSetupScreenMode: CCScreenModeFixed,
		// Run in portrait mode.
		CCSetupScreenOrientation: CCScreenOrientationPortrait,
		// Run at a reduced framerate.
//		CCSetupAnimationInterval: @(1.0/30.0),
		// Run the fixed timestep extra fast.
//		CCSetupFixedUpdateInterval: @(1.0/180.0),
		// Make iPad's act like they run at a 2x content scale. (iPad retina 4x)
//		CCSetupTabletScale2X: @(YES),
	}];
	
	return YES;
}

-(CCScene *)startScene
{
    // load Texture into Cache
    [[CCTextureCache sharedTextureCache] addImage:@"atlas.png"];
    // load atlas infor
    NSError *error;
    NSArray *data = [[NSString stringWithContentsOfFile:[[NSBundle mainBundle] pathForResource: @"atlas" ofType: @"txt"] encoding:NSUTF8StringEncoding error:&error] componentsSeparatedByString: @"\n"];
    NSMutableDictionary* atlasInfo = [NSMutableDictionary dictionaryWithCapacity:data.count];
    for (NSInteger i = data.count - 1; i >= 0; i --) {
        NSArray* datum = [[data objectAtIndex:i] componentsSeparatedByString:@" "];
        [atlasInfo setObject:datum forKey:[datum objectAtIndex:0]];
    }
    GameConfig.atlasInfo = atlasInfo;
    
	// This method should return the very first scene to be run when your app starts.
	return [MenuScene scene];
}

@end

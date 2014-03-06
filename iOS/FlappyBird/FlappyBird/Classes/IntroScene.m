//
//  IntroScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/2/14.
//  Copyright Hai Do Minh 2014. All rights reserved.
//
// -----------------------------------------------------------------------

// Import the interfaces
#import "IntroScene.h"
#import "HelloWorldScene.h"
#import "NewtonScene.h"
#import "GameConfig.h"

// -----------------------------------------------------------------------
#pragma mark - IntroScene
// -----------------------------------------------------------------------

@implementation IntroScene

// -----------------------------------------------------------------------
#pragma mark - Create & Destroy
// -----------------------------------------------------------------------

+ (IntroScene *)scene
{
	return [[self alloc] init];
}

// -----------------------------------------------------------------------

- (id)init
{
    // Apple recommend assigning self with supers return value
    self = [super init];
    if (!self) return(nil);
    
    NSMutableDictionary* atlasInfo = GameConfig.atlasInfo;
    CCTexture *atlas = GameConfig.atlas;
    // Create a colored background (Dark Grey)
    //    CCNodeColor *background = [CCNodeColor nodeWithColor:[CCColor colorWithRed:0.2f green:0.2f blue:0.2f alpha:1.0f]];
    NSArray *bginfo = [atlasInfo objectForKey:@"bg_day"];
    CCSprite *background = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[bginfo objectAtIndex:3] intValue], [[bginfo objectAtIndex:4] intValue], [[bginfo objectAtIndex:1] intValue], [[bginfo objectAtIndex:2] intValue])];
    background.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    background.scaleX = self.contentSize.width / background.contentSize.width;
    background.scaleY = self.contentSize.height / background.contentSize.height;
    [self addChild:background];
    NSArray *footerinfo = [atlasInfo objectForKey:@"land"];
    CCSprite *footer = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue])];
    footer.position = ccp(self.contentSize.width / 2, 0);
    [self addChild:footer];
    NSArray *copyinfo = [atlasInfo objectForKey:@"brand_copyright"];
    CCSprite *copy = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[copyinfo objectAtIndex:3] intValue], [[copyinfo objectAtIndex:4] intValue], [[copyinfo objectAtIndex:1] intValue], [[copyinfo objectAtIndex:2] intValue])];
    copy.position = ccp(self.contentSize.width / 2, copy.contentSize.height / 2 + 5);
    [self addChild:copy];
    
    
    // Hello world
    CCLabelTTF *label = [CCLabelTTF labelWithString:@"Hello World" fontName:@"Chalkduster" fontSize:36.0f];
    label.positionType = CCPositionTypeNormalized;
    label.color = [CCColor redColor];
    label.position = ccp(0.5f, 0.5f); // Middle of screen
    [self addChild:label];
    
    // Spinning scene button
    CCButton *spinningButton = [CCButton buttonWithTitle:@"[ Simple Sprite ]" fontName:@"Verdana-Bold" fontSize:18.0f];
    spinningButton.positionType = CCPositionTypeNormalized;
    spinningButton.position = ccp(0.5f, 0.35f);
    [spinningButton setTarget:self selector:@selector(onSpinningClicked:)];
    [self addChild:spinningButton];
    
    // Next scene button
    CCButton *newtonButton = [CCButton buttonWithTitle:@"[ Newton Physics ]" fontName:@"Verdana-Bold" fontSize:18.0f];
    newtonButton.positionType = CCPositionTypeNormalized;
    newtonButton.position = ccp(0.5f, 0.20f);
    [newtonButton setTarget:self selector:@selector(onNewtonClicked:)];
    [self addChild:newtonButton];
	
    // done
	return self;
}

// -----------------------------------------------------------------------
#pragma mark - Button Callbacks
// -----------------------------------------------------------------------

- (void)onSpinningClicked:(id)sender
{
    // start spinning scene with transition
    [[CCDirector sharedDirector] replaceScene:[HelloWorldScene scene]
                               withTransition:[CCTransition transitionPushWithDirection:CCTransitionDirectionLeft duration:1.0f]];
}

- (void)onNewtonClicked:(id)sender
{
    // start newton scene with transition
    // the current scene is pushed, and thus needs popping to be brought back. This is done in the newton scene, when pressing back (upper left corner)
    [[CCDirector sharedDirector] pushScene:[NewtonScene scene]
                            withTransition:[CCTransition transitionPushWithDirection:CCTransitionDirectionLeft duration:1.0f]];
}

// -----------------------------------------------------------------------
@end

//
//  MenuScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/7/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "MenuScene.h"
#import "GameConfig.h"


@implementation MenuScene

CCSprite *footer;
CCSprite *footer_1;

+ (MenuScene *)scene
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

    NSArray *bginfo = [atlasInfo objectForKey:@"bg_day"];
    CCSprite *background = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[bginfo objectAtIndex:3] intValue], [[bginfo objectAtIndex:4] intValue], [[bginfo objectAtIndex:1] intValue], [[bginfo objectAtIndex:2] intValue])];
    background.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    float scaleX = self.contentSize.width / background.contentSize.width;
    float scaleY = self.contentSize.height / background.contentSize.height;
    background.scaleX = scaleX;
    background.scaleY = scaleY;
    [self addChild:background];
    NSArray *footerinfo = [atlasInfo objectForKey:@"land"];
    footer = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue])];
    footer_1 = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue])];
    footer.position = ccp(footer.contentSize.width / 2, footer.contentSize.height / 2);
    footer_1.position = ccp(footer.contentSize.width * 3 / 2, footer.position.y);
    [self addChild:footer];
    [self addChild:footer_1];
    NSArray *copyinfo = [atlasInfo objectForKey:@"brand_copyright"];
    CCSprite *copy = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[copyinfo objectAtIndex:3] intValue], [[copyinfo objectAtIndex:4] intValue], [[copyinfo objectAtIndex:1] intValue], [[copyinfo objectAtIndex:2] intValue])];
    copy.position = ccp(self.contentSize.width / 2, footer.contentSize.height / 2 );
    [self addChild:copy];
    // done
	return self;
}

-(void) update:(CCTime)delta {
	CGPoint bg1Pos = footer.position;
	CGPoint bg2Pos = footer_1.position;
    
//    NSLog(@"Before: %f; %f; size: %f", bg1Pos.x, bg2Pos.x, footer.contentSize.width /  2);
    
    bg1Pos.x -= 1;
	bg2Pos.x -= 1;
	
	// move scrolling background back by one screen width to achieve "endless" scrolling
	if (bg1Pos.x < - footer.contentSize.width / 2) {
		bg1Pos.x = bg2Pos.x + footer.contentSize.width;
	} else if (bg2Pos.x < - footer_1.contentSize.width / 2) {
		bg2Pos.x = bg1Pos.x + footer_1.contentSize.width;
	}

//    NSLog(@"After: %f; %f; size: %f", bg1Pos.x, bg2Pos.x, footer.contentSize.width /  2);	
    
	// remove any inaccuracies by assigning only int values
	// (prevents floating point rounding errors accumulating over time)
//	bg1Pos.x = (int)bg1Pos.x;
//    bg2Pos.x = (int)bg2Pos.x;
    footer.position = bg1Pos;
    footer_1.position = bg2Pos;
}

@end

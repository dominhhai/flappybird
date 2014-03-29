//
//  Footer.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/23/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import "Footer.h"
#import "GameConfig.h"

@implementation Footer

@synthesize spr_1, spr_2, scene;

-(id)initWithScene:(CCScene*)pScene {
    self = [super init];
    if (!self) return nil;
    
    self.scene = pScene;
    
    NSMutableDictionary* atlasInfo = GameConfig.atlasInfo;
    CCTexture *atlas = GameConfig.atlas;
    
    NSArray *footerinfo = [atlasInfo objectForKey:@"land"];
    CGRect footerRectInPixels = CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue]);
    self.spr_1 = [CCSprite spriteWithTexture:atlas rect:footerRectInPixels];
    self.spr_2 = [CCSprite spriteWithTexture:atlas rect:footerRectInPixels];
    self.spr_1.position = ccp(self.spr_1.contentSize.width / 2, self.spr_1.contentSize.height / 4);
    self.spr_2.position = ccp(self.spr_1.contentSize.width * 3 / 2, self.spr_1.position.y);
    [self.scene addChild:self.spr_1];
    [self.scene addChild:self.spr_2];
    NSArray *copyinfo = [atlasInfo objectForKey:@"brand_copyright"];
    CCSprite *copy = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[copyinfo objectAtIndex:3] intValue], [[copyinfo objectAtIndex:4] intValue], [[copyinfo objectAtIndex:1] intValue], [[copyinfo objectAtIndex:2] intValue])];
    copy.position = ccp(self.scene.contentSize.width / 2, self.spr_1.position.y);
    [self.scene addChild:copy];
    
    return self;
}

-(void) update:(CCTime)delta {
	CGPoint bg1Pos = self.spr_1.position;
	CGPoint bg2Pos = self.spr_2.position;
    
    bg1Pos.x -= 1;
	bg2Pos.x -= 1;
	
	if (bg1Pos.x < - self.spr_1.contentSize.width / 2) {
		bg1Pos.x = bg2Pos.x + self.spr_1.contentSize.width;
	} else if (bg2Pos.x < - self.spr_1.contentSize.width / 2) {
		bg2Pos.x = bg1Pos.x + self.spr_2.contentSize.width;
	}
    
    self.spr_1.position = bg1Pos;
    self.spr_2.position = bg2Pos;
}

@end

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
    // btn Play
    NSArray *btnPlayInfo = [atlasInfo objectForKey:@"button_play"];
    CCSpriteFrame *btnPlayFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:CGRectMake([[btnPlayInfo objectAtIndex:3] intValue], [[btnPlayInfo objectAtIndex:4] intValue], [[btnPlayInfo objectAtIndex:1] intValue], [[btnPlayInfo objectAtIndex:2] intValue]) rotated:false offset:CGPointMake(0, 0) originalSize:CGSizeMake([[btnPlayInfo objectAtIndex:3] intValue], [[btnPlayInfo objectAtIndex:4] intValue])];
    CCButton *btnPlay = [CCButton buttonWithTitle:@"" spriteFrame:btnPlayFrame];
    btnPlay.position = ccp((self.contentSize.width / 2 - [[btnPlayInfo objectAtIndex:0] integerValue]) / 2, self.contentSize.height - footer.position.y - footer.contentSize.height / 2 - 20 - [[btnPlayInfo objectAtIndex:1] integerValue]);
    [btnPlay setTarget:self selector:@selector(onBtnPlayClicked:)];
    [self addChild:btnPlay];
    // btn Score
    NSArray *btnScoreInfo = [atlasInfo objectForKey:@"button_score"];
    CCSpriteFrame *btnScoreFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:CGRectMake([[btnScoreInfo objectAtIndex:3] intValue], [[btnScoreInfo objectAtIndex:4] intValue], [[btnScoreInfo objectAtIndex:1] intValue], [[btnScoreInfo objectAtIndex:2] intValue]) rotated:false offset:CGPointMake(0, 0) originalSize:CGSizeMake([[btnScoreInfo objectAtIndex:3] intValue], [[btnScoreInfo objectAtIndex:4] intValue])];
    CCButton *btnScore = [CCButton buttonWithTitle:@"" spriteFrame:btnScoreFrame];
    btnScore.position = ccp(self.contentSize.width - btnPlay.position.x, btnPlay.position.y);
    [btnScore setTarget:self selector:@selector(onBtnScoreClicked:)];
    [self addChild:btnScore];
    // done
	return self;
}

- (void)onBtnPlayClicked:(id)sender {
    // start spinning scene with transition
    NSLog(@"BtnPlay Clicked");
}

- (void)onBtnScoreClicked:(id)sender {
    // start spinning scene with transition
    NSLog(@"BtnScore Clicked");
}

-(void) update:(CCTime)delta {
	CGPoint bg1Pos = footer.position;
	CGPoint bg2Pos = footer_1.position;
    
    bg1Pos.x -= 1;
	bg2Pos.x -= 1;
	
	// move scrolling background back by one screen width to achieve "endless" scrolling
	if (bg1Pos.x < - footer.contentSize.width / 2) {
		bg1Pos.x = bg2Pos.x + footer.contentSize.width;
	} else if (bg2Pos.x < - footer_1.contentSize.width / 2) {
		bg2Pos.x = bg1Pos.x + footer_1.contentSize.width;
	}

    footer.position = bg1Pos;
    footer_1.position = bg2Pos;
}

@end

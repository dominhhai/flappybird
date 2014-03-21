//
//  MenuScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/7/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "MenuScene.h"
#import "GameConfig.h"
#import "CCAnimation.h"

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
    
    [self setUserInteractionEnabled:YES];
    [self setMultipleTouchEnabled:NO];
    
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
    CGRect footerRectInPixels = CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue]);
    footer = [CCSprite spriteWithTexture:atlas rect:footerRectInPixels];
    footer_1 = [CCSprite spriteWithTexture:atlas rect:footerRectInPixels];
    footer.position = ccp(footer.contentSize.width / 2, footer.contentSize.height / 2);
    footer_1.position = ccp(footer.contentSize.width * 3 / 2, footer.position.y);
    [self addChild:footer];
    [self addChild:footer_1];
    NSArray *copyinfo = [atlasInfo objectForKey:@"brand_copyright"];
    CCSprite *copy = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[copyinfo objectAtIndex:3] intValue], [[copyinfo objectAtIndex:4] intValue], [[copyinfo objectAtIndex:1] intValue], [[copyinfo objectAtIndex:2] intValue])];
    copy.position = ccp(self.contentSize.width / 2, footer.contentSize.height / 2 );
    [self addChild:copy];
    // title&bird
    NSArray *titleInfo = [atlasInfo objectForKey:@"title"];
    NSArray *charInfo_0 = [atlasInfo objectForKey:@"bird0_0"];
    CCSprite *title = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[titleInfo objectAtIndex:3] intValue], [[titleInfo objectAtIndex:4] intValue], [[titleInfo objectAtIndex:1] intValue], [[titleInfo objectAtIndex:2] intValue])];
    CCSprite *bird = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[charInfo_0 objectAtIndex:3] intValue], [[charInfo_0 objectAtIndex:4] intValue], [[charInfo_0 objectAtIndex:1] intValue], [[charInfo_0 objectAtIndex:2] intValue])];
    CGPoint titlePosition = ccp((self.contentSize.width - 5 - bird.contentSize.width) / 2, self.contentSize.height / 2 + title.contentSize.height);
    title.position = titlePosition;
    CGPoint birdPosition = ccp(titlePosition.x + title.contentSize.width / 2 + bird.contentSize.width / 2 + 5, titlePosition.y);
    bird.position = birdPosition;
    [title runAction:[CCActionRepeatForever actionWithAction:[CCActionSequence actions:[CCActionMoveTo actionWithDuration:0.45 position:ccp(titlePosition.x, titlePosition.y - 5)], [CCActionMoveTo actionWithDuration:0.45 position:ccp(titlePosition.x, titlePosition.y + 20)], nil]]];
    [bird runAction:[CCActionRepeatForever actionWithAction:[CCActionSequence actions:[CCActionMoveTo actionWithDuration:0.45 position:ccp(birdPosition.x, birdPosition.y - 5)], [CCActionMoveTo actionWithDuration:0.45 position:ccp(birdPosition.x, birdPosition.y + 20)], nil]]];
    // bird animatin
    NSMutableArray* spriteFrames = [NSMutableArray array];
    for (int i = 0; i < 3; i ++) {
        NSString *resource = [NSString stringWithFormat:@"bird0_%i", i];
        NSArray *charInfo = [atlasInfo objectForKey:resource];
        NSLog(@"Cur: %i: %@", i, resource);
        CGSize birdSizeInPixels = CGSizeMake([[charInfo objectAtIndex:1] intValue], [[charInfo objectAtIndex:2] intValue]);
        CGRect birdRectInPixels = {CGPointMake([[charInfo objectAtIndex:3] intValue], [[charInfo objectAtIndex:4] intValue]), birdSizeInPixels};
        CCSpriteFrame* spriteFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:birdRectInPixels rotated:NO offset:CGPointZero originalSize:birdSizeInPixels];
        [spriteFrames addObject:spriteFrame];
    }
    CCAnimation* animation = [CCAnimation animationWithSpriteFrames: spriteFrames delay:0.1];
    CCActionAnimate* actionAnimate = [CCActionAnimate actionWithAnimation:animation];
    [bird runAction:[CCActionRepeatForever actionWithAction:actionAnimate]];
    [self addChild:title];
    [self addChild:bird];
    // btn Play
    NSArray *btnPlayInfo = [atlasInfo objectForKey:@"button_play"];
    CGSize btnSizeInPixels = CGSizeMake([[btnPlayInfo objectAtIndex:1] intValue], [[btnPlayInfo objectAtIndex:2] intValue]);
    CGRect btnPlayRectInPixels = {CGPointMake([[btnPlayInfo objectAtIndex:3] intValue], [[btnPlayInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    CCSpriteFrame *btnPlayFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnPlayRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    CCButton *btnPlay = [CCButton buttonWithTitle:@"" spriteFrame:btnPlayFrame];
    CGPoint btnPlayPosition = ccp(self.contentSize.width / 4, footer.position.y + footer.contentSize.height / 2 + 20 + btnSizeInPixels.height / 2);
    btnPlay.position = btnPlayPosition;
    [btnPlay setTarget:self selector:@selector(onBtnPlayClicked:)];
    [self addChild:btnPlay];
    // btn Score
    NSArray *btnScoreInfo = [atlasInfo objectForKey:@"button_score"];
    CGRect btnScoreRectInPixels = {CGPointMake([[btnScoreInfo objectAtIndex:3] intValue], [[btnScoreInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    CCSpriteFrame *btnScoreFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnScoreRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    CCButton *btnScore = [CCButton buttonWithTitle:@"" spriteFrame:btnScoreFrame];
    btnScore.position = ccp(self.contentSize.width - btnPlayPosition.x, btnPlayPosition.y);
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

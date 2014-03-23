//
//  MenuScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/7/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "MenuScene.h"
#import "GameConfig.h"
#import "Bird.h"
#import "Footer.h"
#import "PlayScene.h"

@implementation MenuScene

+ (MenuScene *)scene
{
	return [[self alloc] init];
}

// -----------------------------------------------------------------------

Footer* footer;

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
    // footer
    footer = [[Footer alloc] initWithScene:self];
    // title&bird
    NSArray *titleInfo = [atlasInfo objectForKey:@"title"];
    NSArray *charInfo_0 = [atlasInfo objectForKey:@"bird0_0"];
    CCSprite *title = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[titleInfo objectAtIndex:3] intValue], [[titleInfo objectAtIndex:4] intValue], [[titleInfo objectAtIndex:1] intValue], [[titleInfo objectAtIndex:2] intValue])];
    CGPoint titlePosition = ccp((self.contentSize.width - 5 - [[charInfo_0 objectAtIndex:1] intValue]) / 2, self.contentSize.height / 2 + title.contentSize.height);
    title.position = titlePosition;
    CGPoint birdPosition = ccp(titlePosition.x + title.contentSize.width / 2 + [[charInfo_0 objectAtIndex:1] intValue] / 2 + 5, titlePosition.y);
    [title runAction:[CCActionRepeatForever actionWithAction:[CCActionSequence actions:[CCActionMoveTo actionWithDuration:0.45 position:ccp(titlePosition.x, titlePosition.y - 5)], [CCActionMoveTo actionWithDuration:0.45 position:ccp(titlePosition.x, titlePosition.y + 20)], nil]]];
    [self addChild:title];
    Bird* bird = [[Bird alloc] initWithType:BIRD_TYPE_YELLOW position:birdPosition scene:self];
    // btn Play
    NSArray *btnPlayInfo = [atlasInfo objectForKey:@"button_play"];
    CGSize btnSizeInPixels = CGSizeMake([[btnPlayInfo objectAtIndex:1] intValue], [[btnPlayInfo objectAtIndex:2] intValue]);
    CGRect btnPlayRectInPixels = {CGPointMake([[btnPlayInfo objectAtIndex:3] intValue], [[btnPlayInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    CCSpriteFrame *btnPlayFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnPlayRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    CCButton *btnPlay = [CCButton buttonWithTitle:@"" spriteFrame:btnPlayFrame];
    CGPoint btnPlayPosition = ccp(self.contentSize.width / 4, footer.spr_1.position.y + footer.spr_1.contentSize.height / 2 + 20 + btnSizeInPixels.height / 2);
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
    [[CCDirector sharedDirector] replaceScene:[PlayScene scene]];
    [self removeAllChildrenWithCleanup:YES];
    [self removeFromParent];
}

- (void)onBtnScoreClicked:(id)sender {
    // start spinning scene with transition
    NSLog(@"BtnScore Clicked");
}

-(void) update:(CCTime)delta {
	[footer update:delta];
}

@end
